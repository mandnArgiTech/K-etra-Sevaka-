package com.ksetrasevakah.feature.pumpiq.prediction.algorithm

import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import com.ksetrasevakah.designsystem.model.RiskLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ForgotOffPredictorTest {

    private lateinit var predictor: ForgotOffPredictor

    @BeforeEach
    fun setup() {
        predictor = ForgotOffPredictor()
    }

    private fun activity(daysAgo: Int, forgotOff: Boolean = false): WorkerActivityEntity {
        val date = LocalDate.now().minusDays(daysAgo.toLong())
            .format(DateTimeFormatter.ISO_LOCAL_DATE)
        return WorkerActivityEntity(
            date = date,
            onTime = System.currentTimeMillis() - daysAgo * 86_400_000L,
            offTime = System.currentTimeMillis() - daysAgo * 86_400_000L + 8 * 3_600_000L,
            durationMinutes = 480,
            forgotOff = forgotOff
        )
    }

    @Test
    fun `returns insufficient data when fewer than 5 activities`() {
        val activities = (1..4).map { activity(it) }
        val result = predictor.predict(activities)
        assertTrue(result.insufficientData)
    }

    @Test
    fun `returns zero risk when no forgot-off events`() {
        val activities = (1..10).map { activity(it, forgotOff = false) }
        val result = predictor.predict(activities)
        assertFalse(result.insufficientData)
        assertEquals(0, result.historicalForgotCount)
        assertEquals(RiskLevel.LOW, result.riskLevel)
    }

    @Test
    fun `computes correct forgot count`() {
        val activities = (1..10).map { activity(it, forgotOff = it <= 3) }
        val result = predictor.predict(activities)
        assertEquals(3, result.historicalForgotCount)
        assertEquals(10, result.totalDays)
    }

    @Test
    fun `assigns higher risk for higher forgot rate`() {
        val lowRiskActivities = (1..10).map { activity(it, forgotOff = it == 1) }
        val highRiskActivities = (1..10).map { activity(it, forgotOff = it <= 7) }

        val lowResult = predictor.predict(lowRiskActivities)
        val highResult = predictor.predict(highRiskActivities)

        assertTrue(highResult.riskPercent > lowResult.riskPercent)
    }

    @Test
    fun `risk percent stays within 0-100 bounds`() {
        val allForgot = (1..10).map { activity(it, forgotOff = true) }
        val result = predictor.predict(allForgot)
        assertTrue(result.riskPercent in 0..100)
    }

    @Test
    fun `computes day-of-week factor`() {
        val activities = (0..13).map { activity(it, forgotOff = it % 7 < 2) }
        val result = predictor.predict(activities)
        assertFalse(result.insufficientData)
        assertTrue(result.riskPercent >= 0)
    }
}
