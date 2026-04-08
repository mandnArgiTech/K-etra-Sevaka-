package com.ksetrasevakah.designsystem.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RiskLevelTest {

    @Test
    fun `fromPercentage 10 returns LOW`() {
        assertEquals(RiskLevel.LOW, RiskLevel.fromPercentage(10))
    }

    @Test
    fun `fromPercentage 30 returns MEDIUM`() {
        assertEquals(RiskLevel.MEDIUM, RiskLevel.fromPercentage(30))
    }

    @Test
    fun `fromPercentage 60 returns HIGH`() {
        assertEquals(RiskLevel.HIGH, RiskLevel.fromPercentage(60))
    }

    @Test
    fun `fromPercentage 80 returns CRITICAL`() {
        assertEquals(RiskLevel.CRITICAL, RiskLevel.fromPercentage(80))
    }

    @Test
    fun `boundary 24 returns LOW`() {
        assertEquals(RiskLevel.LOW, RiskLevel.fromPercentage(24))
    }

    @Test
    fun `boundary 25 returns MEDIUM`() {
        assertEquals(RiskLevel.MEDIUM, RiskLevel.fromPercentage(25))
    }
}
