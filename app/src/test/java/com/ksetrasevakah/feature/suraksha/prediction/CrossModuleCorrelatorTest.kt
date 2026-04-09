package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.FaultEntity
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.domain.repository.FaultRepository
import com.ksetrasevakah.core.domain.repository.TelemetryRepository
import com.ksetrasevakah.designsystem.model.RiskLevel
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
    private lateinit var telemetryRepository: TelemetryRepository
    private lateinit var faultRepository: FaultRepository
    private lateinit var correlator: CrossModuleCorrelator

    @BeforeEach
    fun setup() {
        securityEventRepository = mockk(relaxed = true)
        telemetryRepository = mockk(relaxed = true)
        faultRepository = mockk(relaxed = true)
        correlator = CrossModuleCorrelator(
            securityEventRepository,
            telemetryRepository,
            faultRepository
        )
    }

    private fun secPerson(ts: Long) = SecurityEvent(
        id = 1L,
        cameraName = "Transformer Cam",
        eventType = EventType.PERSON,
        threatLevel = ThreatLevel.HIGH,
        confidence = 0.9f,
        originTimestamp = ts,
        receivedTimestamp = ts,
        hourOfDay = 22
    )

    private fun secTamper(ts: Long) = SecurityEvent(
        id = 2L,
        cameraName = "Gate",
        eventType = EventType.TAMPERING,
        threatLevel = ThreatLevel.CRITICAL,
        confidence = 0.95f,
        originTimestamp = ts,
        receivedTimestamp = ts,
        hourOfDay = 3
    )

    @Test
    fun `PERSON near power telemetry yields PERSON_NEAR_POWER_FAILURE`() = runTest {
        val t0 = 1_000_000L
        coEvery { securityEventRepository.getRecentEventsSince(any()) } returns Result.Success(listOf(secPerson(t0)))
        coEvery { telemetryRepository.getRecentSince(any()) } returns Result.Success(
            listOf(
                TelemetryEntity(
                    rawSms = "",
                    timestamp = t0 + 120_000L,
                    motorOn = false,
                    narrative = "Power outage detected at panel"
                )
            )
        )
        coEvery { faultRepository.getRecentSince(any()) } returns Result.Success(emptyList())

        val result = correlator.findCrossModuleCorrelations(0L, windowMs = 300_000L)
        assertTrue(result is Result.Success)
        val list = (result as Result.Success).data
        assertEquals(1, list.size)
        assertEquals("PERSON_NEAR_POWER_FAILURE", list[0].correlationType)
        assertEquals(RiskLevel.HIGH, list[0].severity)
    }

    @Test
    fun `TAMPERING near fault yields TAMPERING_PLUS_FAULT CRITICAL`() = runTest {
        val t0 = 2_000_000L
        coEvery { securityEventRepository.getRecentEventsSince(any()) } returns Result.Success(listOf(secTamper(t0)))
        coEvery { telemetryRepository.getRecentSince(any()) } returns Result.Success(emptyList())
        coEvery { faultRepository.getRecentSince(any()) } returns Result.Success(
            listOf(
                FaultEntity(
                    timestamp = t0 + 120_000L,
                    faultType = "DRY_RUN",
                    description = "Dry run"
                )
            )
        )

        val result = correlator.findCrossModuleCorrelations(0L, windowMs = 300_000L)
        assertTrue(result is Result.Success)
        val list = (result as Result.Success).data
        assertEquals(1, list.size)
        assertEquals("TAMPERING_PLUS_FAULT", list[0].correlationType)
        assertEquals(RiskLevel.CRITICAL, list[0].severity)
    }

    @Test
    fun `no security events returns empty`() = runTest {
        coEvery { securityEventRepository.getRecentEventsSince(any()) } returns Result.Success(emptyList())
        coEvery { telemetryRepository.getRecentSince(any()) } returns Result.Success(
            listOf(
                TelemetryEntity(
                    rawSms = "",
                    timestamp = 1L,
                    motorOn = true,
                    narrative = "Power outage"
                )
            )
        )

        val result = correlator.findCrossModuleCorrelations(0L)
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun `no PumpIQ data still returns success for person event`() = runTest {
        coEvery { securityEventRepository.getRecentEventsSince(any()) } returns Result.Success(listOf(secPerson(1L)))
        coEvery { telemetryRepository.getRecentSince(any()) } returns Result.Success(emptyList())
        coEvery { faultRepository.getRecentSince(any()) } returns Result.Success(emptyList())

        val result = correlator.findCrossModuleCorrelations(0L)
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun `security repository error propagates`() = runTest {
        coEvery { securityEventRepository.getRecentEventsSince(any()) } returns Result.Error("db")

        val result = correlator.findCrossModuleCorrelations(0L)
        assertTrue(result is Result.Error)
    }
}
