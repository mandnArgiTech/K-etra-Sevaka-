package com.ksetrasevakah.feature.pumpiq.prediction

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.PredictionCacheDao
import com.ksetrasevakah.core.database.entity.PredictionCacheEntity
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import com.ksetrasevakah.core.domain.repository.FaultRepository
import com.ksetrasevakah.core.domain.repository.TelemetryRepository
import com.ksetrasevakah.core.domain.repository.WorkerActivityRepository
import com.ksetrasevakah.designsystem.model.RiskLevel
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.FaultPredictor
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.ForgotOffPredictor
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.PowerFailurePredictor
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.WorkerOnTimePredictor
import com.ksetrasevakah.feature.pumpiq.prediction.model.FaultPrediction
import com.ksetrasevakah.feature.pumpiq.prediction.model.ForgotOffPrediction
import com.ksetrasevakah.feature.pumpiq.prediction.model.PowerFailurePrediction
import com.ksetrasevakah.feature.pumpiq.prediction.model.WorkerOnPrediction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PredictionEngineTest {

    private lateinit var telemetryRepository: TelemetryRepository
    private lateinit var faultRepository: FaultRepository
    private lateinit var workerActivityRepository: WorkerActivityRepository
    private lateinit var predictionCacheDao: PredictionCacheDao
    private lateinit var powerFailurePredictor: PowerFailurePredictor
    private lateinit var faultPredictor: FaultPredictor
    private lateinit var workerOnTimePredictor: WorkerOnTimePredictor
    private lateinit var forgotOffPredictor: ForgotOffPredictor
    private lateinit var engine: PredictionEngine

    @BeforeEach
    fun setup() {
        telemetryRepository = mockk(relaxed = true)
        faultRepository = mockk(relaxed = true)
        workerActivityRepository = mockk(relaxed = true)
        predictionCacheDao = mockk(relaxed = true)
        powerFailurePredictor = mockk(relaxed = true)
        faultPredictor = mockk(relaxed = true)
        workerOnTimePredictor = mockk(relaxed = true)
        forgotOffPredictor = mockk(relaxed = true)

        engine = PredictionEngine(
            telemetryRepository = telemetryRepository,
            faultRepository = faultRepository,
            workerActivityRepository = workerActivityRepository,
            predictionCacheDao = predictionCacheDao,
            powerFailurePredictor = powerFailurePredictor,
            faultPredictor = faultPredictor,
            workerOnTimePredictor = workerOnTimePredictor,
            forgotOffPredictor = forgotOffPredictor
        )

        coEvery { telemetryRepository.getRecentList(any()) } returns Result.Success(emptyList())
        coEvery { faultRepository.getFaultDistribution(any()) } returns Result.Success(emptyList())
        every { faultRepository.observeRecent(any()) } returns flowOf(Result.Success(emptyList()))
        every { workerActivityRepository.observeLast14Days() } returns flowOf(Result.Success(emptyList()))
        coEvery { predictionCacheDao.getByType(any()) } returns null

        every { powerFailurePredictor.predict(any()) } returns PowerFailurePrediction(insufficientData = true)
        every { faultPredictor.predict(any(), any()) } returns FaultPrediction(insufficientData = true)
        every { workerOnTimePredictor.predict(any()) } returns WorkerOnPrediction(insufficientData = true)
        every { forgotOffPredictor.predict(any()) } returns ForgotOffPrediction(insufficientData = true)
    }

    @Test
    fun `predictAll returns result from all four predictors`() = runTest {
        val result = engine.predictAll(forceRefresh = true)

        assertNotNull(result.powerFailure)
        assertNotNull(result.fault)
        assertNotNull(result.workerOn)
        assertNotNull(result.forgotOff)
    }

    @Test
    fun `predictAll caches results`() = runTest {
        engine.predictAll(forceRefresh = true)

        coVerify { predictionCacheDao.upsert(any()) }
    }

    @Test
    fun `predictAll uses cache when valid`() = runTest {
        val cached = PredictionCacheEntity(
            type = "predictions_all",
            resultJson = "pf_h=15|pf_v=190.0|pf_c=0.5|pf_r=MEDIUM|ft_h=48.0|ft_t=overvoltage|ft_c=0.3|ft_r=LOW|wo_h=6|wo_m=30|wo_c=0.8|fo_p=20|fo_r=LOW",
            confidence = 0.5f,
            computedAt = System.currentTimeMillis(),
            validUntil = System.currentTimeMillis() + 60_000L
        )
        coEvery { predictionCacheDao.getByType("predictions_all") } returns cached

        val result = engine.predictAll(forceRefresh = false)

        assertEquals(15, result.powerFailure.predictedTimeHour)
        assertEquals(RiskLevel.MEDIUM, result.powerFailure.riskLevel)
    }

    @Test
    fun `predictAll bypasses expired cache`() = runTest {
        val expired = PredictionCacheEntity(
            type = "predictions_all",
            resultJson = "pf_h=15|pf_v=190.0|pf_c=0.5|pf_r=MEDIUM|ft_h=48.0|ft_t=overvoltage|ft_c=0.3|ft_r=LOW|wo_h=6|wo_m=30|wo_c=0.8|fo_p=20|fo_r=LOW",
            confidence = 0.5f,
            computedAt = System.currentTimeMillis() - 120_000L,
            validUntil = System.currentTimeMillis() - 60_000L
        )
        coEvery { predictionCacheDao.getByType("predictions_all") } returns expired

        engine.predictAll(forceRefresh = false)

        coVerify { predictionCacheDao.upsert(any()) }
    }
}
