package com.ksetrasevakah.feature.pumpiq.prediction

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.core.domain.repository.WorkerActivityRepository
import com.ksetrasevakah.designsystem.model.MotorState
import javax.inject.Inject

data class ForgotOffAlert(
    val thresholdMinutes: Int,
    val currentSessionMinutes: Long,
    val avgOffTimeMinutes: Long
)

class ForgotOffWatchdog @Inject constructor(
    private val motorStateRepository: MotorStateRepository,
    private val workerActivityRepository: WorkerActivityRepository
) {
    companion object {
        val ALERT_THRESHOLDS_MINUTES = listOf(45, 75, 105)
    }

    suspend fun check(): ForgotOffAlert? {
        val sessionResult = motorStateRepository.getSessionDuration()
        val sessionMs = sessionResult.getOrNull() ?: return null
        if (sessionMs <= 0) return null

        val sessionMinutes = sessionMs / 60_000L

        val avgOnTimeResult = workerActivityRepository.getAvgOnTime(
            Constants.PREDICTION_WINDOW_DAYS
        )
        val avgOnTimeMs = avgOnTimeResult.getOrNull() ?: return null
        val avgOnTimeMinutes = avgOnTimeMs / 60_000L

        if (avgOnTimeMinutes <= 0) return null

        val elapsedPastAvg = sessionMinutes - avgOnTimeMinutes

        val breachedThreshold = ALERT_THRESHOLDS_MINUTES
            .sortedDescending()
            .firstOrNull { threshold -> elapsedPastAvg >= threshold }

        return breachedThreshold?.let {
            ForgotOffAlert(
                thresholdMinutes = it,
                currentSessionMinutes = sessionMinutes,
                avgOffTimeMinutes = avgOnTimeMinutes
            )
        }
    }
}
