package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.ai.MlcLlmEngine
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.domain.model.EventType
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SecurityBriefingGeneratorTest {

    private lateinit var engine: MlcLlmEngine
    private lateinit var securityEventRepository: SecurityEventRepository
    private lateinit var generator: SecurityBriefingGenerator

    @BeforeEach
    fun setup() {
        engine = mockk()
        securityEventRepository = mockk()
        generator = SecurityBriefingGenerator(engine, securityEventRepository)
    }

    @Test
    fun `generates briefing successfully`() = runTest {
        val events = listOf(
            SecurityEvent(
                id = 1L,
                cameraName = "Gate Camera",
                eventType = EventType.PERSON,
                threatLevel = ThreatLevel.MEDIUM,
                confidence = 0.8f,
                originTimestamp = System.currentTimeMillis(),
                receivedTimestamp = System.currentTimeMillis(),
                hourOfDay = 14,
                description = "Person at gate"
            )
        )
        val counts = mapOf(ThreatLevel.MEDIUM to 1)

        coEvery { securityEventRepository.getEventsInWindow(any()) } returns Result.Success(events)
        coEvery { securityEventRepository.getThreatCounts() } returns Result.Success(counts)
        every { engine.generate(any(), any()) } returns flowOf("Farm is secure today.")

        val result = generator.generate()
        assertTrue(result is Result.Success)
        assertEquals("Farm is secure today.", (result as Result.Success).data)
    }

    @Test
    fun `returns error when events fetch fails`() = runTest {
        coEvery { securityEventRepository.getEventsInWindow(any()) } returns
            Result.Error("Failed to load events")

        val result = generator.generate()
        assertTrue(result is Result.Error)
        assertEquals("Failed to load events", (result as Result.Error).message)
    }

    @Test
    fun `returns error when model returns empty briefing`() = runTest {
        coEvery { securityEventRepository.getEventsInWindow(any()) } returns Result.Success(emptyList())
        coEvery { securityEventRepository.getThreatCounts() } returns Result.Success(emptyMap())
        every { engine.generate(any(), any()) } returns flowOf("")

        val result = generator.generate()
        assertTrue(result is Result.Error)
        assertEquals("Model returned empty briefing", (result as Result.Error).message)
    }

    @Test
    fun `returns error on exception`() = runTest {
        coEvery { securityEventRepository.getEventsInWindow(any()) } throws
            RuntimeException("Unexpected crash")

        val result = generator.generate()
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Unexpected crash"))
    }
}
