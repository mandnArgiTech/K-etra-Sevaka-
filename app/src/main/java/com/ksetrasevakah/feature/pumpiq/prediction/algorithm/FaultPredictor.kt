package com.ksetrasevakah.feature.pumpiq.prediction.algorithm

import com.ksetrasevakah.core.database.entity.FaultEntity
import com.ksetrasevakah.designsystem.model.RiskLevel
import com.ksetrasevakah.feature.pumpiq.prediction.model.FaultPrediction
import com.ksetrasevakah.feature.pumpiq.prediction.model.RiskTimelineEntry
import java.util.Calendar
import javax.inject.Inject

class FaultPredictor @Inject constructor() {

    companion object {
        private const val MIN_FAULTS = 3
        private const val TIMELINE_WINDOWS = 6
        private const val HOURS_PER_WINDOW = 4f
    }

    fun predict(faults: List<FaultEntity>, windowDays: Int): FaultPrediction {
        if (faults.size < MIN_FAULTS) {
            return FaultPrediction(insufficientData = true)
        }

        val sorted = faults.sortedBy { it.timestamp }
        val intervals = sorted.zipWithNext { a, b ->
            (b.timestamp - a.timestamp).toFloat() / 3_600_000f
        }
        val avgIntervalHours = intervals.average().toFloat()

        val lastFaultTime = sorted.last().timestamp
        val hoursSinceLast = (System.currentTimeMillis() - lastFaultTime).toFloat() / 3_600_000f
        val nextFaultHours = (avgIntervalHours - hoursSinceLast).coerceAtLeast(0f)

        val typeFrequency = faults.groupingBy { it.faultType }.eachCount()
        val mostLikelyType = typeFrequency.maxByOrNull { it.value }?.key

        val timeline = buildTimeline(avgIntervalHours, hoursSinceLast)

        val faultRate = faults.size.toFloat() / windowDays.coerceAtLeast(1)
        val riskPct = (faultRate * 20).toInt().coerceIn(0, 100)
        val riskLevel = RiskLevel.fromPercentage(riskPct)

        val confidence = (faults.size.toFloat() / (faults.size + 5f))
            .coerceIn(0f, 1f)

        return FaultPrediction(
            nextFaultHours = nextFaultHours,
            mostLikelyType = mostLikelyType,
            riskTimeline = timeline,
            riskLevel = riskLevel,
            confidence = confidence
        )
    }

    private fun buildTimeline(
        avgIntervalHours: Float,
        hoursSinceLast: Float
    ): List<RiskTimelineEntry> {
        return (1..TIMELINE_WINDOWS).map { window ->
            val hoursFromNow = window * HOURS_PER_WINDOW
            val totalElapsed = hoursSinceLast + hoursFromNow
            val riskPercent = ((totalElapsed / avgIntervalHours) * 50)
                .toInt().coerceIn(0, 100)
            RiskTimelineEntry(
                hoursFromNow = hoursFromNow,
                riskPercent = riskPercent,
                riskLevel = RiskLevel.fromPercentage(riskPercent)
            )
        }
    }
}
