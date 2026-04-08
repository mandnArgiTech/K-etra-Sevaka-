package com.ksetrasevakah.feature.pumpiq.prediction.algorithm

import com.ksetrasevakah.core.database.entity.FaultEntity
import com.ksetrasevakah.designsystem.model.RiskLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FaultPredictorTest {

    private lateinit var predictor: FaultPredictor

    @BeforeEach
    fun setup() {
        predictor = FaultPredictor()
    }

    private fun fault(
        type: String,
        hoursAgo: Int
    ): FaultEntity {
        return FaultEntity(
            timestamp = System.currentTimeMillis() - hoursAgo * 3_600_000L,
            faultType = type,
            description = "Test fault: $type"
        )
    }

    @Test
    fun `returns insufficient data when fewer than 3 faults`() {
        val faults = listOf(
            fault("overvoltage", 48),
            fault("overcurrent", 24)
        )
        val result = predictor.predict(faults, 14)
        assertTrue(result.insufficientData)
    }

    @Test
    fun `predicts next fault hours correctly`() {
        val faults = listOf(
            fault("overvoltage", 72),
            fault("overcurrent", 48),
            fault("overvoltage", 24),
            fault("overcurrent", 1)
        )
        val result = predictor.predict(faults, 14)
        assertFalse(result.insufficientData)
        assertNotNull(result.nextFaultHours)
        assertTrue(result.nextFaultHours!! >= 0f)
    }

    @Test
    fun `identifies most likely fault type`() {
        val faults = listOf(
            fault("overvoltage", 100),
            fault("overvoltage", 80),
            fault("overvoltage", 60),
            fault("overcurrent", 40)
        )
        val result = predictor.predict(faults, 14)
        assertEquals("overvoltage", result.mostLikelyType)
    }

    @Test
    fun `builds risk timeline with 6 entries`() {
        val faults = (1..5).map { fault("overvoltage", it * 24) }
        val result = predictor.predict(faults, 14)
        assertEquals(6, result.riskTimeline.size)
        assertTrue(result.riskTimeline.all { it.hoursFromNow > 0f })
    }

    @Test
    fun `computes confidence proportional to fault count`() {
        val fewFaults = (1..3).map { fault("overvoltage", it * 24) }
        val manyFaults = (1..20).map { fault("overvoltage", it * 12) }

        val resultFew = predictor.predict(fewFaults, 14)
        val resultMany = predictor.predict(manyFaults, 14)

        assertTrue(resultMany.confidence > resultFew.confidence)
    }
}
