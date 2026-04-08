package com.ksetrasevakah.feature.pumpiq.prediction.algorithm

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.designsystem.model.RiskLevel
import com.ksetrasevakah.feature.pumpiq.prediction.model.PowerFailurePrediction
import java.util.Calendar
import javax.inject.Inject

class PowerFailurePredictor @Inject constructor() {

    companion object {
        private const val DANGER_VOLTAGE = 200f
        private const val MIN_DATA_POINTS = 10
    }

    fun predict(telemetry: List<TelemetryEntity>): PowerFailurePrediction {
        if (telemetry.size < MIN_DATA_POINTS) {
            return PowerFailurePrediction(insufficientData = true)
        }

        val calendar = Calendar.getInstance()
        val hourlyVoltages = mutableMapOf<Int, MutableList<Float>>()

        for (entry in telemetry) {
            val voltage = entry.voltage ?: continue
            calendar.timeInMillis = entry.timestamp
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            hourlyVoltages.getOrPut(hour) { mutableListOf() }.add(voltage)
        }

        val dipHours = hourlyVoltages.filter { (_, voltages) ->
            voltages.average() < DANGER_VOLTAGE
        }

        if (dipHours.isEmpty()) {
            val totalDays = computeTotalDays(telemetry)
            return PowerFailurePrediction(
                confidence = 0.8f,
                riskLevel = RiskLevel.LOW,
                historicalDipCount = 0,
                totalDays = totalDays
            )
        }

        val worstHour = dipHours.minByOrNull { (_, voltages) -> voltages.average() }!!
        val avgVoltage = worstHour.value.average().toFloat()
        val totalDipCount = dipHours.values.sumOf { it.size }
        val totalDays = computeTotalDays(telemetry)

        val dipRate = if (totalDays > 0) totalDipCount.toFloat() / totalDays else 0f
        val confidence = (dipRate * 0.6f + (1f - avgVoltage / DANGER_VOLTAGE) * 0.4f)
            .coerceIn(0f, 1f)

        val riskPct = ((1f - avgVoltage / 240f) * 100).toInt().coerceIn(0, 100)
        val riskLevel = RiskLevel.fromPercentage(riskPct)

        return PowerFailurePrediction(
            predictedTimeHour = worstHour.key,
            predictedTimeMinute = 0,
            predictedVoltage = avgVoltage,
            confidence = confidence,
            riskLevel = riskLevel,
            historicalDipCount = totalDipCount,
            totalDays = totalDays
        )
    }

    private fun computeTotalDays(telemetry: List<TelemetryEntity>): Int {
        if (telemetry.isEmpty()) return 0
        val calendar = Calendar.getInstance()
        val days = telemetry.map { entry ->
            calendar.timeInMillis = entry.timestamp
            calendar.get(Calendar.DAY_OF_YEAR) + calendar.get(Calendar.YEAR) * 366
        }.distinct()
        return days.size.coerceAtLeast(1)
    }
}
