package com.ksetrasevakah.core.data.repository

import app.cash.turbine.test
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.MotorStateDao
import com.ksetrasevakah.core.database.entity.MotorStateEntity
import com.ksetrasevakah.designsystem.model.MotorState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MotorStateRepositoryImplTest {

    private lateinit var dao: MotorStateDao
    private lateinit var repository: MotorStateRepositoryImpl

    @BeforeEach
    fun setup() {
        dao = mockk(relaxed = true)
        repository = MotorStateRepositoryImpl(dao)
    }

    @Test
    fun `observeMotorState emits Success with mapped MotorState`() = runTest {
        val entity = MotorStateEntity(state = "ON")
        every { dao.observe() } returns flowOf(entity)

        repository.observeMotorState().test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(MotorState.ON, (result as Result.Success).data)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `observeMotorState emits Success OFF when entity is null`() = runTest {
        every { dao.observe() } returns flowOf(null)

        repository.observeMotorState().test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(MotorState.OFF, (result as Result.Success).data)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `observeMotorState emits Error for invalid state string`() = runTest {
        val entity = MotorStateEntity(state = "INVALID_STATE")
        every { dao.observe() } returns flowOf(entity)

        repository.observeMotorState().test {
            val result = awaitItem()
            assertTrue(result.isError)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateMotorState returns Success on happy path`() = runTest {
        coEvery { dao.updateState(any(), any()) } returns Unit

        val result = repository.updateMotorState(MotorState.ON)
        assertTrue(result.isSuccess)
        coVerify { dao.updateState("ON", any()) }
    }

    @Test
    fun `updateMotorState returns Error when DAO throws`() = runTest {
        coEvery { dao.updateState(any(), any()) } throws RuntimeException("DB error")

        val result = repository.updateMotorState(MotorState.ON)
        assertTrue(result.isError)
        assertEquals("DB error", (result as Result.Error).message)
    }

    @Test
    fun `setPendingCommand returns Success on happy path`() = runTest {
        coEvery { dao.setPendingCommand(any(), any(), any()) } returns Unit

        val result = repository.setPendingCommand("START")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `setPendingCommand returns Error when DAO throws`() = runTest {
        coEvery { dao.setPendingCommand(any(), any(), any()) } throws RuntimeException("DB error")

        val result = repository.setPendingCommand("START")
        assertTrue(result.isError)
    }

    @Test
    fun `clearPendingCommand returns Success on happy path`() = runTest {
        coEvery { dao.setPendingCommand(null, null, any()) } returns Unit

        val result = repository.clearPendingCommand()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `clearPendingCommand returns Error when DAO throws`() = runTest {
        coEvery { dao.setPendingCommand(null, null, any()) } throws RuntimeException("DB error")

        val result = repository.clearPendingCommand()
        assertTrue(result.isError)
    }

    @Test
    fun `getSessionDuration returns duration when session is active`() = runTest {
        val startTime = System.currentTimeMillis() - 60_000L
        val entity = MotorStateEntity(state = "ON", currentSessionStart = startTime)
        coEvery { dao.get() } returns entity

        val result = repository.getSessionDuration()
        assertTrue(result.isSuccess)
        val duration = (result as Result.Success).data
        assertTrue(duration >= 60_000L)
    }

    @Test
    fun `getSessionDuration returns 0 when no active session`() = runTest {
        val entity = MotorStateEntity(state = "OFF", currentSessionStart = null)
        coEvery { dao.get() } returns entity

        val result = repository.getSessionDuration()
        assertTrue(result.isSuccess)
        assertEquals(0L, (result as Result.Success).data)
    }

    @Test
    fun `getSessionDuration returns Error when DAO throws`() = runTest {
        coEvery { dao.get() } throws RuntimeException("DB error")

        val result = repository.getSessionDuration()
        assertTrue(result.isError)
    }
}
