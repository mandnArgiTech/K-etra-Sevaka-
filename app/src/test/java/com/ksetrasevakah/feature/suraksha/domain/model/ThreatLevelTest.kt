package com.ksetrasevakah.feature.suraksha.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ThreatLevelTest {

    @Test
    fun `isAlertable returns true for HIGH and CRITICAL`() {
        assertTrue(ThreatLevel.HIGH.isAlertable)
        assertTrue(ThreatLevel.CRITICAL.isAlertable)
    }

    @Test
    fun `isAlertable returns false for LOW and MEDIUM`() {
        assertFalse(ThreatLevel.LOW.isAlertable)
        assertFalse(ThreatLevel.MEDIUM.isAlertable)
    }

    @Test
    fun `isCritical returns true only for CRITICAL`() {
        assertTrue(ThreatLevel.CRITICAL.isCritical)
        assertFalse(ThreatLevel.HIGH.isCritical)
        assertFalse(ThreatLevel.MEDIUM.isCritical)
        assertFalse(ThreatLevel.LOW.isCritical)
    }

    @Test
    fun `fromString parses case-insensitive values`() {
        assertEquals(ThreatLevel.LOW, ThreatLevel.fromString("low"))
        assertEquals(ThreatLevel.MEDIUM, ThreatLevel.fromString("Medium"))
        assertEquals(ThreatLevel.HIGH, ThreatLevel.fromString("HIGH"))
        assertEquals(ThreatLevel.CRITICAL, ThreatLevel.fromString("critical"))
    }

    @Test
    fun `fromString returns LOW for unknown values`() {
        assertEquals(ThreatLevel.LOW, ThreatLevel.fromString("unknown"))
        assertEquals(ThreatLevel.LOW, ThreatLevel.fromString(""))
        assertEquals(ThreatLevel.LOW, ThreatLevel.fromString("invalid"))
    }

    @Test
    fun `elevate steps LOW to MEDIUM to HIGH to CRITICAL`() {
        assertEquals(ThreatLevel.MEDIUM, ThreatLevel.LOW.elevate())
        assertEquals(ThreatLevel.HIGH, ThreatLevel.MEDIUM.elevate())
        assertEquals(ThreatLevel.CRITICAL, ThreatLevel.HIGH.elevate())
        assertEquals(ThreatLevel.CRITICAL, ThreatLevel.CRITICAL.elevate())
    }

    @Test
    fun `elevate NONE becomes LOW`() {
        assertEquals(ThreatLevel.LOW, ThreatLevel.NONE.elevate())
    }
}
