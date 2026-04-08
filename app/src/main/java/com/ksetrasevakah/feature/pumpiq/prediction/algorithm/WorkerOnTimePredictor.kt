package com.ksetrasevakah.feature.pumpiq.prediction.algorithm

import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import com.ksetrasevakah.feature.pumpiq.prediction.model.WorkerOnPrediction
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.sqrt

class WorkerOnTimePredictor @Inject constructor() {

    companion object {
        private const val MIN_DAYS = 3
    }

    fun predict(activities: List<WorkerActivityEntity>): WorkerOnPrediction {
        val validActivities = activities.filter { it.onTime != null }

        if (validActivities.size < MIN_DAYS) {
            return WorkerOnPrediction(insufficientData = true)
        }

        val calendar = Calendar.getInstance()
        val onTimeMinutes = validActivities.map { activity ->
            calendar.timeInMillis = activity.onTime!!
            calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
        }

        val mean = onTimeMinutes.average()
        val variance = onTimeMinutes.map { (it - mean) * (it - mean) }.average()
        val stdDev = sqrt(variance).toFloat()

        val predictedHour = (mean / 60).toInt()
        val predictedMinute = (mean % 60).toInt()

        val confidence = (1f - stdDev / 120f).coerceIn(0.1f, 1f)

        return WorkerOnPrediction(
            predictedHour = predictedHour,
            predictedMinute = predictedMinute,
            confidence = confidence,
            standardDeviationMinutes = stdDev
        )
    }
}
