package com.ksetrasevakah.core.notification

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TapoNotificationParserTest {

    @Test
    fun `extractCameraName from title with colon prefix`() {
        val name = TapoNotificationParser.extractCameraName("Front Door: motion detected", "")
        assertEquals("Front Door", name)
    }

    @Test
    fun `extractCameraName from bracket notation in text`() {
        val name = TapoNotificationParser.extractCameraName("Alert", "Person detected [Backyard Cam]")
        assertEquals("Backyard Cam", name)
    }

    @Test
    fun `extractCameraName from body with from keyword`() {
        val name = TapoNotificationParser.extractCameraName("Alert", "Motion detected from Garden Camera")
        assertEquals("Garden Camera", name)
    }

    @Test
    fun `extractCameraName returns unknown_camera when no pattern matches`() {
        val name = TapoNotificationParser.extractCameraName("Alert", "Something happened")
        assertEquals("unknown_camera", name)
    }

    @Test
    fun `extractEventType detects person keywords`() {
        assertEquals("PERSON", TapoNotificationParser.extractEventType("Person detected", ""))
        assertEquals("PERSON", TapoNotificationParser.extractEventType("", "someone is at the door"))
        assertEquals("PERSON", TapoNotificationParser.extractEventType("Motion Alert", ""))
    }

    @Test
    fun `extractEventType detects tampering keywords`() {
        assertEquals("TAMPERING", TapoNotificationParser.extractEventType("Camera tampering", ""))
        assertEquals("TAMPERING", TapoNotificationParser.extractEventType("", "lens blocked detected"))
    }

    @Test
    fun `extractEventType returns UNKNOWN for unrecognized text`() {
        assertEquals("UNKNOWN", TapoNotificationParser.extractEventType("Alert", "noise detected"))
    }

    @Test
    fun `parse creates TapoEvent with all fields`() {
        val event = TapoNotificationParser.parse(
            title = "Driveway: Person detected",
            text = "A person was seen",
            whenMs = 1000L
        )
        assertEquals("Driveway", event.cameraName)
        assertEquals("PERSON", event.eventType)
        assertEquals(1000L, event.timestamp)
        assertEquals("Driveway: Person detected", event.rawTitle)
        assertEquals("A person was seen", event.rawText)
    }
}
