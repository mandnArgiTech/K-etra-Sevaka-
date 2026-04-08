package com.ksetrasevakah.feature.pumpiq.prediction.algorithm

import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Calendar

class WorkerOnTimePredictorTest {

    private lateinit var predictor: WorkerOnTimePredictor

    @BeforeEach
    fun setup() {
        predictor = WorkerOnTimePredictor()
    }

    private fun activity(
        date: String,
        onHour: Int,
        onMinute: Int
    ): WorkerActivityEntity {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, onHour)
            set(Calendar.MINUTE, onMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return WorkerActivityEntity(
            date = date,
            onTime = calendar.timeInMillis,
            offTime = calendar.timeInMillis + 8 * 3600_000L,
            durationMinutes = 480
        )
    }

    @Test
    fun `returns insufficient data when fewer than 3 activities`() {
        val activities = listOf(
            activity("2025-01-01", 6, 30),
            activity("2025-01-02", 7, 0)
        )
        val result = predictor.predict(activities)
        assertTrue(result.insufficientData)
    }

    @Test
    fun `predicts average ON hour and minute`() {
        val activities = listOf(
            activity("2025-01-01", 6, 0),
            activity("2025-01-02", 6, 30),
            activity("2025-01-03", 7, 0)
        )
        val result = predictor.predict(activities)
        assertFalse(result.insufficientData)
        assertEquals(6, result.predictedHour)
        assertTrue(result.predictedMinute!! in 8..22)
    }

    @Test
    fun `computes standard deviation`() {
        val activities = listOf(
            activity("2025-01-01", 6, 0),
            activity("2025-01-02", 6, 30),
            activity("2025-01-03", 7, 0)
        )
        val result = predictor.predict(activities)
        assertTrue(result.standardDeviationMinutes > 0f)
    }

    @Test
    fun `high consistency yields high confidence`() {
        val activities = (1..10).map {
            activity("2025-01-%02d".format(it), 6, 30)
        }
        val result = predictor.predict(activities)
        assertTrue(result.confidence > 0.9f)
    }

    @Test
    fun `skips entries with null onTime`() {
        val activities = listOf(
            activity("2025-01-01", 6, 30),
            WorkerActivityEntity(date = "2025-01-02", onTime = null),
            activity("2025-01-03", 7, 0),
            activity("2025-01-04", 6, 45)
        )
        val result = predictor.predict(activities)
        assertFalse(result.insufficientData)
    }
}
