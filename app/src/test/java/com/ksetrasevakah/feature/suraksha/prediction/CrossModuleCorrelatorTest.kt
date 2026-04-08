package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.domain.model.EventType
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CrossModuleCorrelatorTest {

    private lateinit var securityEventRepository: SecurityEventRepository
    private lateinit var correlator: CrossModuleCorrelator

    @BeforeEach
    fun setup() {
        securityEventRepository = mockk()
        correlator = CrossModuleCorrelator(securityEventRepository)
    }

    private fun makeEvent(
        id: Long,
        timestamp: Long,
        eventType: EventType,
        threatLevel: ThreatLevel
    ) = SecurityEvent(
        id = id,
        cameraName = "TestCam",
        eventType = eventType,
        threatLevel = threatLevel,
        confidence = 0.8f,
        originTimestamp = timestamp,
        receivedTimestamp = timestamp,
        hourOfDay = 12
    )

    @Test
    fun `returns empty list when fewer than 2 events`() = runTest {
        val singleEvent = makeEvent(1L, System.currentTimeMillis(), EventType.UNKNOWN, ThreatLevel.LOW)
        coEvery { securityEventRepository.getEventsInWindow(any()) } returns
            Result.Success(listOf(singleEvent))

        val result = correlator.findTemporalCorrelations()
        assertTrue(result is Result.Success)
        assertEquals(0, (result as Result.Success).data.size)
    }

    @Test
    fun `finds correlation when events cluster within window`() = runTest {
        val now = System.currentTimeMillis()
        val events = listOf(
            makeEvent(1L, now, EventType.PERSON, ThreatLevel.MEDIUM),
            makeEvent(2L, now + 30_000L, EventType.TAMPERING, ThreatLevel.HIGH),
            makeEvent(3L, now + 60_000L, EventType.UNKNOWN, ThreatLevel.LOW)
        )
        coEvery { securityEventRepository.getEventsInWindow(any()) } returns
            Result.Success(events)

        val result = correlator.findTemporalCorrelations()
        assertTrue(result is Result.Success)
        val correlations = (result as Result.Success).data
        assertTrue(correlations.isNotEmpty())
        assertTrue(correlations.first().securityEvents.size >= 2)
    }

    @Test
    fun `correlation score is between 0 and 1`() = runTest {
        val now = System.currentTimeMillis()
        val events = listOf(
            makeEvent(1L, now, EventType.PERSON, ThreatLevel.MEDIUM),
            makeEvent(2L, now + 1000L, EventType.TAMPERING, ThreatLevel.HIGH)
        )
        coEvery { securityEventRepository.getEventsInWindow(any()) } returns
            Result.Success(events)

        val result = correlator.findTemporalCorrelations()
        assertTrue(result is Result.Success)
        val correlations = (result as Result.Success).data
        correlations.forEach { correlation ->
            assertTrue(correlation.correlationScore in 0f..1f)
        }
    }

    @Test
    fun `returns error when repository fails`() = runTest {
        coEvery { securityEventRepository.getEventsInWindow(any()) } returns
            Result.Error("DB error")

        val result = correlator.findTemporalCorrelations()
        assertTrue(result is Result.Error)
        assertEquals("DB error", (result as Result.Error).message)
    }

    @Test
    fun `returns empty list for empty events`() = runTest {
        coEvery { securityEventRepository.getEventsInWindow(any()) } returns
            Result.Success(emptyList())

        val result = correlator.findTemporalCorrelations()
        assertTrue(result is Result.Success)
        assertEquals(0, (result as Result.Success).data.size)
    }
}
