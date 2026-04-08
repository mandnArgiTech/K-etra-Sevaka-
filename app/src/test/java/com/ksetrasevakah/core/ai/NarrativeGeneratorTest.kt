package com.ksetrasevakah.core.ai

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NarrativeGeneratorTest {

    private lateinit var engine: MlcLlmEngine
    private lateinit var generator: NarrativeGenerator

    private val sampleTelemetry = TelemetryEntity(
        id = 1,
        rawSms = "MOTOR ON R=4.2 Y=4.1 B=4.0 V=230",
        timestamp = System.currentTimeMillis(),
        motorOn = true,
        phaseR = 4.2f,
        phaseY = 4.1f,
        phaseB = 4.0f,
        voltage = 230f,
        temperature = 45f,
        runtimeMinutes = 120
    )

    @BeforeEach
    fun setup() {
        engine = mockk()
        generator = NarrativeGenerator(engine)
    }

    @Test
    fun `generate returns narrative on success`() = runTest {
        every { engine.generate(any(), any()) } returns flowOf("Motor ", "is ", "running ", "normally.")

        val result = generator.generate(sampleTelemetry)

        assertTrue(result is Result.Success)
        assertEquals("Motor is running normally.", (result as Result.Success).data)
    }

    @Test
    fun `generate returns Error when model returns blank`() = runTest {
        every { engine.generate(any(), any()) } returns flowOf("  ", " ")

        val result = generator.generate(sampleTelemetry)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `generate returns Error on exception`() = runTest {
        every { engine.generate(any(), any()) } returns flow {
            throw RuntimeException("Model crashed")
        }

        val result = generator.generate(sampleTelemetry)

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Model crashed"))
    }

    @Test
    fun `generate trims whitespace from narrative`() = runTest {
        every { engine.generate(any(), any()) } returns flowOf("  Motor running.  ")

        val result = generator.generate(sampleTelemetry)

        assertTrue(result is Result.Success)
        assertEquals("Motor running.", (result as Result.Success).data)
    }
}
