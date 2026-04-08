package com.ksetrasevakah.feature.pumpiq.prediction.algorithm

import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.designsystem.model.RiskLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Calendar

class PowerFailurePredictorTest {

    private lateinit var predictor: PowerFailurePredictor

    @BeforeEach
    fun setup() {
        predictor = PowerFailurePredictor()
    }

    private fun telemetry(
        hour: Int,
        voltage: Float,
        dayOffset: Int = 0
    ): TelemetryEntity {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            add(Calendar.DAY_OF_YEAR, -dayOffset)
        }
        return TelemetryEntity(
            rawSms = "test",
            timestamp = calendar.timeInMillis,
            motorOn = true,
            voltage = voltage
        )
    }

    @Test
    fun `returns insufficient data when fewer than 10 entries`() {
        val data = (1..5).map { telemetry(12, 230f, it) }
        val result = predictor.predict(data)
        assertTrue(result.insufficientData)
    }

    @Test
    fun `returns LOW risk when no voltage dips below 200V`() {
        val data = (1..14).map { telemetry(it % 24, 235f, it) }
        val result = predictor.predict(data)
        assertFalse(result.insufficientData)
        assertEquals(RiskLevel.LOW, result.riskLevel)
        assertEquals(0, result.historicalDipCount)
    }

    @Test
    fun `identifies hour with lowest average voltage`() {
        val normalData = (1..10).map { telemetry(10, 235f, it) }
        val dipData = (1..5).map { telemetry(15, 180f, it) }
        val result = predictor.predict(normalData + dipData)
        assertFalse(result.insufficientData)
        assertEquals(15, result.predictedTimeHour)
        assertNotNull(result.predictedVoltage)
    }

    @Test
    fun `computes non-zero confidence for voltage dips`() {
        val data = (1..14).flatMap { day ->
            listOf(
                telemetry(10, 230f, day),
                telemetry(15, 180f, day)
            )
        }
        val result = predictor.predict(data)
        assertTrue(result.confidence > 0f)
    }

    @Test
    fun `assigns higher risk for lower voltages`() {
        val data = (1..14).flatMap { day ->
            listOf(
                telemetry(10, 235f, day),
                telemetry(15, 120f, day)
            )
        }
        val result = predictor.predict(data)
        assertTrue(result.riskLevel >= RiskLevel.MEDIUM)
    }

    @Test
    fun `counts total days correctly`() {
        val data = (0..13).map { telemetry(12, 190f, it) }
        val result = predictor.predict(data)
        assertTrue(result.totalDays >= 14)
    }
}
