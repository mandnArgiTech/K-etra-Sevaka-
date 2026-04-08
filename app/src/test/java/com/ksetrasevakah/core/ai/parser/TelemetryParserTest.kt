package com.ksetrasevakah.core.ai.parser

import com.ksetrasevakah.core.common.Result
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TelemetryParserTest {

    @Test
    fun `parseResponse extracts valid JSON`() {
        val json = """{"motorOn":true,"phaseR":4.2,"phaseY":4.1,"phaseB":4.0,"voltage":230.0,"temperature":45.0,"runtimeMinutes":120}"""

        val result = TelemetryParser.parseResponse(json)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(true, data.motorOn)
        assertEquals(4.2f, data.phaseR)
        assertEquals(4.1f, data.phaseY)
        assertEquals(4.0f, data.phaseB)
        assertEquals(230.0f, data.voltage)
        assertEquals(45.0f, data.temperature)
        assertEquals(120, data.runtimeMinutes)
    }

    @Test
    fun `parseResponse handles JSON with surrounding text`() {
        val json = """Here is the extracted data: {"motorOn":false,"phaseR":null,"voltage":220.5} some trailing text"""

        val result = TelemetryParser.parseResponse(json)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(false, data.motorOn)
        assertNull(data.phaseR)
        assertEquals(220.5f, data.voltage)
    }

    @Test
    fun `parseResponse handles null fields`() {
        val json = """{"motorOn":true,"phaseR":null,"phaseY":null,"phaseB":null,"voltage":null,"temperature":null,"runtimeMinutes":null}"""

        val result = TelemetryParser.parseResponse(json)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(true, data.motorOn)
        assertNull(data.phaseR)
        assertNull(data.voltage)
        assertNull(data.runtimeMinutes)
    }

    @Test
    fun `regex fallback parses common SMS format`() {
        val sms = "MOTOR ON R=4.2 Y=4.1 B=4.0 V=230 Temp=45 Runtime=120"

        val result = TelemetryParser.tryRegexFallback(sms)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(true, data.motorOn)
        assertEquals(4.2f, data.phaseR)
        assertEquals(4.1f, data.phaseY)
        assertEquals(4.0f, data.phaseB)
        assertEquals(230f, data.voltage)
        assertEquals(45f, data.temperature)
        assertEquals(120, data.runtimeMinutes)
    }

    @Test
    fun `regex fallback returns Error when no data extracted`() {
        val garbage = "Hello World no data here"

        val result = TelemetryParser.tryRegexFallback(garbage)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `parseResponse falls back to regex on invalid JSON`() {
        val text = "MOTOR ON confirmed R:4.5 Y:4.3 B:4.1 Voltage:228"

        val result = TelemetryParser.parseResponse(text)

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(true, data.motorOn)
        assertEquals(4.5f, data.phaseR)
        assertEquals(228f, data.voltage)
    }
}
