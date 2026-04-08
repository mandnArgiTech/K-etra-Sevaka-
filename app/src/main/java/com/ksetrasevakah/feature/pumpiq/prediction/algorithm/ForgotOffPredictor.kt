package com.ksetrasevakah.feature.pumpiq.prediction.algorithm

import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import com.ksetrasevakah.designsystem.model.RiskLevel
import com.ksetrasevakah.feature.pumpiq.prediction.model.ForgotOffPrediction
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ForgotOffPredictor @Inject constructor() {

    companion object {
        private const val MIN_DAYS = 5
        private val WEEKEND_DAYS = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    }

    fun predict(activities: List<WorkerActivityEntity>): ForgotOffPrediction {
        if (activities.size < MIN_DAYS) {
            return ForgotOffPrediction(insufficientData = true)
        }

        val forgotCount = activities.count { it.forgotOff }
        val totalDays = activities.size
        val baseRate = forgotCount.toFloat() / totalDays

        val dayOfWeekFactor = computeDayOfWeekFactor(activities)
        val adjustedRate = (baseRate * dayOfWeekFactor).coerceIn(0f, 1f)
        val riskPct = (adjustedRate * 100).toInt().coerceIn(0, 100)

        return ForgotOffPrediction(
            riskPercent = riskPct,
            riskLevel = RiskLevel.fromPercentage(riskPct),
            historicalForgotCount = forgotCount,
            totalDays = totalDays
        )
    }

    private fun computeDayOfWeekFactor(activities: List<WorkerActivityEntity>): Float {
        val today = LocalDate.now().dayOfWeek

        val forgotByWeekend = activities.count { activity ->
            val dayOfWeek = parseDayOfWeek(activity.date)
            activity.forgotOff && dayOfWeek != null && dayOfWeek in WEEKEND_DAYS
        }
        val forgotByWeekday = activities.count { activity ->
            val dayOfWeek = parseDayOfWeek(activity.date)
            activity.forgotOff && dayOfWeek != null && dayOfWeek !in WEEKEND_DAYS
        }

        val weekendDays = activities.count { parseDayOfWeek(it.date) in WEEKEND_DAYS }
            .coerceAtLeast(1)
        val weekdayDays = activities.count { parseDayOfWeek(it.date) !in WEEKEND_DAYS }
            .coerceAtLeast(1)

        val weekendRate = forgotByWeekend.toFloat() / weekendDays
        val weekdayRate = forgotByWeekday.toFloat() / weekdayDays

        return if (today in WEEKEND_DAYS) {
            if (weekdayRate > 0) weekendRate / weekdayRate else 1f
        } else {
            if (weekendRate > 0) weekdayRate / weekendRate else 1f
        }.coerceIn(0.5f, 2f)
    }

    private fun parseDayOfWeek(dateStr: String): DayOfWeek? {
        return try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE).dayOfWeek
        } catch (_: Exception) {
            null
        }
    }
}
