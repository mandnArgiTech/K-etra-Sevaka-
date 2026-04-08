package com.ksetrasevakah.feature.suraksha.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CameraModeTest {

    @Test
    fun `shouldProcess returns true for ACTIVE and SILENT`() {
        assertTrue(CameraMode.ACTIVE.shouldProcess)
        assertTrue(CameraMode.SILENT.shouldProcess)
    }

    @Test
    fun `shouldProcess returns false for DROP`() {
        assertFalse(CameraMode.DROP.shouldProcess)
    }

    @Test
    fun `shouldAlert returns true only for ACTIVE`() {
        assertTrue(CameraMode.ACTIVE.shouldAlert)
        assertFalse(CameraMode.SILENT.shouldAlert)
        assertFalse(CameraMode.DROP.shouldAlert)
    }

    @Test
    fun `fromString parses case-insensitive and defaults to ACTIVE`() {
        assertEquals(CameraMode.ACTIVE, CameraMode.fromString("active"))
        assertEquals(CameraMode.SILENT, CameraMode.fromString("Silent"))
        assertEquals(CameraMode.DROP, CameraMode.fromString("DROP"))
        assertEquals(CameraMode.ACTIVE, CameraMode.fromString("unknown"))
    }
}
