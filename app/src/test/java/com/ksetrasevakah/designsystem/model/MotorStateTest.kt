package com.ksetrasevakah.designsystem.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MotorStateTest {

    @Test
    fun `OFF state properties are correct`() {
        val state = MotorState.OFF
        assertTrue(state.isOff)
        assertFalse(state.isOn)
        assertFalse(state.isPending)
    }

    @Test
    fun `ON state properties are correct`() {
        val state = MotorState.ON
        assertFalse(state.isOff)
        assertTrue(state.isOn)
        assertFalse(state.isPending)
    }

    @Test
    fun `PENDING_START is pending`() {
        assertTrue(MotorState.PENDING_START.isPending)
        assertFalse(MotorState.PENDING_START.isOn)
        assertFalse(MotorState.PENDING_START.isOff)
    }

    @Test
    fun `PENDING_STOP is pending`() {
        assertTrue(MotorState.PENDING_STOP.isPending)
    }

    @Test
    fun `displayLabel returns Offline for OFF`() {
        assertEquals("Offline", MotorState.OFF.displayLabel)
    }

    @Test
    fun `displayLabel returns Running for ON`() {
        assertEquals("Running", MotorState.ON.displayLabel)
    }

    @Test
    fun `displayLabel returns Starting for PENDING_START`() {
        assertEquals("Starting...", MotorState.PENDING_START.displayLabel)
    }

    @Test
    fun `displayLabel returns Stopping for PENDING_STOP`() {
        assertEquals("Stopping...", MotorState.PENDING_STOP.displayLabel)
    }
}
