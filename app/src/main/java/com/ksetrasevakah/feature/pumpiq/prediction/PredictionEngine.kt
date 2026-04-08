package com.ksetrasevakah.feature.pumpiq.prediction

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.database.dao.PredictionCacheDao
import com.ksetrasevakah.core.database.entity.PredictionCacheEntity
import com.ksetrasevakah.core.domain.repository.FaultRepository
import com.ksetrasevakah.core.domain.repository.TelemetryRepository
import com.ksetrasevakah.core.domain.repository.WorkerActivityRepository
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.FaultPredictor
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.ForgotOffPredictor
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.PowerFailurePredictor
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.WorkerOnTimePredictor
import com.ksetrasevakah.feature.pumpiq.prediction.model.FaultPrediction
import com.ksetrasevakah.feature.pumpiq.prediction.model.ForgotOffPrediction
import com.ksetrasevakah.feature.pumpiq.prediction.model.PowerFailurePrediction
import com.ksetrasevakah.feature.pumpiq.prediction.model.WorkerOnPrediction
import javax.inject.Inject
import javax.inject.Singleton

data class PredictionResult(
    val powerFailure: PowerFailurePrediction,
    val fault: FaultPrediction,
    val workerOn: WorkerOnPrediction,
    val forgotOff: ForgotOffPrediction
)

@Singleton
class PredictionEngine @Inject constructor(
    private val telemetryRepository: TelemetryRepository,
    private val faultRepository: FaultRepository,
    private val workerActivityRepository: WorkerActivityRepository,
    private val predictionCacheDao: PredictionCacheDao,
    private val powerFailurePredictor: PowerFailurePredictor,
    private val faultPredictor: FaultPredictor,
    private val workerOnTimePredictor: WorkerOnTimePredictor,
    private val forgotOffPredictor: ForgotOffPredictor
) {
    companion object {
        private const val CACHE_TYPE_PREDICTIONS = "predictions_all"
        private const val CACHE_VALIDITY_MS = 30 * 60 * 1000L
    }

    suspend fun predictAll(forceRefresh: Boolean = false): PredictionResult {
        if (!forceRefresh) {
            val cached = predictionCacheDao.getByType(CACHE_TYPE_PREDICTIONS)
            if (cached != null && cached.validUntil > System.currentTimeMillis()) {
                return parseCachedResult(cached) ?: computeAndCache()
            }
        }
        return computeAndCache()
    }

    private suspend fun computeAndCache(): PredictionResult {
        val windowDays = Constants.PREDICTION_WINDOW_DAYS

        val powerFailure = computePowerFailure(windowDays)
        val fault = computeFault(windowDays)
        val workerOn = computeWorkerOn(windowDays)
        val forgotOff = computeForgotOff(windowDays)

        val result = PredictionResult(powerFailure, fault, workerOn, forgotOff)

        val avgConfidence = listOf(
            powerFailure.confidence,
            fault.confidence,
            workerOn.confidence
        ).average().toFloat()

        val now = System.currentTimeMillis()
        predictionCacheDao.upsert(
            PredictionCacheEntity(
                type = CACHE_TYPE_PREDICTIONS,
                resultJson = serializeResult(result),
                confidence = avgConfidence,
                computedAt = now,
                validUntil = now + CACHE_VALIDITY_MS
            )
        )

        return result
    }

    private suspend fun computePowerFailure(windowDays: Int): PowerFailurePrediction {
        val telemetryResult = telemetryRepository.getRecentList(windowDays)
        val telemetry = telemetryResult.getOrNull() ?: emptyList()
        return powerFailurePredictor.predict(telemetry)
    }

    private suspend fun computeFault(windowDays: Int): FaultPrediction {
        val faultResult = faultRepository.getFaultDistribution(windowDays)
        return if (faultResult.isSuccess) {
            val faults = getFaultEntities(windowDays)
            faultPredictor.predict(faults, windowDays)
        } else {
            FaultPrediction(insufficientData = true)
        }
    }

    private suspend fun getFaultEntities(windowDays: Int): List<com.ksetrasevakah.core.database.entity.FaultEntity> {
        val since = System.currentTimeMillis() - windowDays * 24 * 60 * 60 * 1000L
        val flow = faultRepository.observeRecent(windowDays)
        var faults = emptyList<com.ksetrasevakah.core.database.entity.FaultEntity>()
        flow.collect { result ->
            faults = result.getOrNull() ?: emptyList()
            return@collect
        }
        return faults
    }

    private suspend fun computeWorkerOn(windowDays: Int): WorkerOnPrediction {
        val activitiesResult = workerActivityRepository.observeLast14Days()
        var activities = emptyList<com.ksetrasevakah.core.database.entity.WorkerActivityEntity>()
        activitiesResult.collect { result ->
            activities = result.getOrNull() ?: emptyList()
            return@collect
        }
        return workerOnTimePredictor.predict(activities)
    }

    private suspend fun computeForgotOff(windowDays: Int): ForgotOffPrediction {
        val activitiesResult = workerActivityRepository.observeLast14Days()
        var activities = emptyList<com.ksetrasevakah.core.database.entity.WorkerActivityEntity>()
        activitiesResult.collect { result ->
            activities = result.getOrNull() ?: emptyList()
            return@collect
        }
        return forgotOffPredictor.predict(activities)
    }

    private fun serializeResult(result: PredictionResult): String {
        return buildString {
            append("pf_h=${result.powerFailure.predictedTimeHour}")
            append("|pf_v=${result.powerFailure.predictedVoltage}")
            append("|pf_c=${result.powerFailure.confidence}")
            append("|pf_r=${result.powerFailure.riskLevel}")
            append("|ft_h=${result.fault.nextFaultHours}")
            append("|ft_t=${result.fault.mostLikelyType}")
            append("|ft_c=${result.fault.confidence}")
            append("|ft_r=${result.fault.riskLevel}")
            append("|wo_h=${result.workerOn.predictedHour}")
            append("|wo_m=${result.workerOn.predictedMinute}")
            append("|wo_c=${result.workerOn.confidence}")
            append("|fo_p=${result.forgotOff.riskPercent}")
            append("|fo_r=${result.forgotOff.riskLevel}")
        }
    }

    @Suppress("ReturnCount")
    private fun parseCachedResult(entity: PredictionCacheEntity): PredictionResult? {
        return try {
            val parts = entity.resultJson.split("|").associate {
                val (key, value) = it.split("=", limit = 2)
                key to value
            }
            PredictionResult(
                powerFailure = PowerFailurePrediction(
                    predictedTimeHour = parts["pf_h"]?.toIntOrNull(),
                    predictedVoltage = parts["pf_v"]?.toFloatOrNull(),
                    confidence = parts["pf_c"]?.toFloatOrNull() ?: 0f,
                    riskLevel = parts["pf_r"]?.let { parseRiskLevel(it) }
                        ?: com.ksetrasevakah.designsystem.model.RiskLevel.LOW
                ),
                fault = FaultPrediction(
                    nextFaultHours = parts["ft_h"]?.toFloatOrNull(),
                    mostLikelyType = parts["ft_t"]?.takeIf { it != "null" },
                    confidence = parts["ft_c"]?.toFloatOrNull() ?: 0f,
                    riskLevel = parts["ft_r"]?.let { parseRiskLevel(it) }
                        ?: com.ksetrasevakah.designsystem.model.RiskLevel.LOW
                ),
                workerOn = WorkerOnPrediction(
                    predictedHour = parts["wo_h"]?.toIntOrNull(),
                    predictedMinute = parts["wo_m"]?.toIntOrNull(),
                    confidence = parts["wo_c"]?.toFloatOrNull() ?: 0f
                ),
                forgotOff = ForgotOffPrediction(
                    riskPercent = parts["fo_p"]?.toIntOrNull() ?: 0,
                    riskLevel = parts["fo_r"]?.let { parseRiskLevel(it) }
                        ?: com.ksetrasevakah.designsystem.model.RiskLevel.LOW
                )
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun parseRiskLevel(value: String): com.ksetrasevakah.designsystem.model.RiskLevel {
        return try {
            com.ksetrasevakah.designsystem.model.RiskLevel.valueOf(value)
        } catch (_: Exception) {
            com.ksetrasevakah.designsystem.model.RiskLevel.LOW
        }
    }
}
