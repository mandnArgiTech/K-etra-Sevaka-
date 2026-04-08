package com.ksetrasevakah.feature.pumpiq.prediction

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.core.domain.repository.WorkerActivityRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ForgotOffWatchdogTest {

    private lateinit var motorStateRepository: MotorStateRepository
    private lateinit var workerActivityRepository: WorkerActivityRepository
    private lateinit var watchdog: ForgotOffWatchdog

    @BeforeEach
    fun setup() {
        motorStateRepository = mockk(relaxed = true)
        workerActivityRepository = mockk(relaxed = true)
        watchdog = ForgotOffWatchdog(motorStateRepository, workerActivityRepository)
    }

    @Test
    fun `returns null when no active session`() = runTest {
        coEvery { motorStateRepository.getSessionDuration() } returns Result.Success(0L)

        val result = watchdog.check()

        assertNull(result)
    }

    @Test
    fun `returns null when session is within avg time`() = runTest {
        val avgOnTimeMs = 480L * 60_000L
        val sessionMs = avgOnTimeMs + 10L * 60_000L

        coEvery { motorStateRepository.getSessionDuration() } returns Result.Success(sessionMs)
        coEvery { workerActivityRepository.getAvgOnTime(any()) } returns Result.Success(avgOnTimeMs)

        val result = watchdog.check()

        assertNull(result)
    }

    @Test
    fun `returns alert at 45 minute threshold`() = runTest {
        val avgOnTimeMs = 480L * 60_000L
        val sessionMs = avgOnTimeMs + 50L * 60_000L

        coEvery { motorStateRepository.getSessionDuration() } returns Result.Success(sessionMs)
        coEvery { workerActivityRepository.getAvgOnTime(any()) } returns Result.Success(avgOnTimeMs)

        val result = watchdog.check()

        assertNotNull(result)
        assertEquals(45, result!!.thresholdMinutes)
    }

    @Test
    fun `returns alert at 75 minute threshold`() = runTest {
        val avgOnTimeMs = 480L * 60_000L
        val sessionMs = avgOnTimeMs + 80L * 60_000L

        coEvery { motorStateRepository.getSessionDuration() } returns Result.Success(sessionMs)
        coEvery { workerActivityRepository.getAvgOnTime(any()) } returns Result.Success(avgOnTimeMs)

        val result = watchdog.check()

        assertNotNull(result)
        assertEquals(75, result!!.thresholdMinutes)
    }

    @Test
    fun `returns alert at 105 minute threshold`() = runTest {
        val avgOnTimeMs = 480L * 60_000L
        val sessionMs = avgOnTimeMs + 120L * 60_000L

        coEvery { motorStateRepository.getSessionDuration() } returns Result.Success(sessionMs)
        coEvery { workerActivityRepository.getAvgOnTime(any()) } returns Result.Success(avgOnTimeMs)

        val result = watchdog.check()

        assertNotNull(result)
        assertEquals(105, result!!.thresholdMinutes)
    }
}
