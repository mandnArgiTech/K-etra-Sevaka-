package com.ksetrasevakah.core.sms

import com.ksetrasevakah.core.database.dao.MotorStateDao
import com.ksetrasevakah.core.database.dao.WorkerActivityDao
import com.ksetrasevakah.core.database.entity.MotorStateEntity
import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import com.ksetrasevakah.designsystem.model.MotorState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MotorStateMachineTest {

    private lateinit var motorStateDao: MotorStateDao
    private lateinit var workerActivityDao: WorkerActivityDao
    private lateinit var stateMachine: MotorStateMachine

    @BeforeEach
    fun setup() {
        motorStateDao = mockk(relaxed = true)
        workerActivityDao = mockk(relaxed = true)
        stateMachine = MotorStateMachine(motorStateDao, workerActivityDao)
    }

    @Test
    fun `sendStart from OFF transitions to PENDING_START`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.OFF)

        val result = stateMachine.sendStart()

        assertEquals(MotorState.PENDING_START, result)
        coVerify { motorStateDao.updateState(MotorState.PENDING_START.name, any()) }
    }

    @Test
    fun `sendStart from ON stays ON`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.ON)

        val result = stateMachine.sendStart()

        assertEquals(MotorState.ON, result)
        coVerify(exactly = 0) { motorStateDao.updateState(MotorState.PENDING_START.name, any()) }
    }

    @Test
    fun `sendStart from PENDING_START stays PENDING_START`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.PENDING_START)

        val result = stateMachine.sendStart()

        assertEquals(MotorState.PENDING_START, result)
    }

    @Test
    fun `sendStop from ON transitions to PENDING_STOP`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.ON)

        val result = stateMachine.sendStop()

        assertEquals(MotorState.PENDING_STOP, result)
        coVerify { motorStateDao.updateState(MotorState.PENDING_STOP.name, any()) }
    }

    @Test
    fun `sendStop from OFF stays OFF`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.OFF)

        val result = stateMachine.sendStop()

        assertEquals(MotorState.OFF, result)
    }

    @Test
    fun `processConfirmation MOTOR ON transitions to ON`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.PENDING_START)
        coEvery { workerActivityDao.getByDate(any()) } returns null

        val result = stateMachine.processConfirmation("MOTOR ON confirmed")

        assertEquals(MotorState.ON, result)
        coVerify { motorStateDao.setSessionStart(any(), any()) }
    }

    @Test
    fun `processConfirmation MOTOR OFF transitions to OFF`() = runTest {
        val onTime = System.currentTimeMillis() - 60_000
        coEvery { motorStateDao.get() } returns entity(MotorState.PENDING_STOP)
        coEvery { workerActivityDao.getByDate(any()) } returns
            WorkerActivityEntity(date = "2025-01-01", onTime = onTime)

        val result = stateMachine.processConfirmation("MOTOR OFF confirmed")

        assertEquals(MotorState.OFF, result)
        coVerify { motorStateDao.setSessionEnd(any(), any()) }
    }

    @Test
    fun `processConfirmation unknown body keeps current state`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.ON)

        val result = stateMachine.processConfirmation("UNKNOWN MESSAGE")

        assertEquals(MotorState.ON, result)
    }

    @Test
    fun `handleTimeout reverts PENDING_START to OFF after 30s`() = runTest {
        val expired = System.currentTimeMillis() - 31_000
        coEvery { motorStateDao.get() } returns entity(
            MotorState.PENDING_START, pendingSince = expired
        )

        stateMachine.handleTimeout()

        coVerify { motorStateDao.updateState(MotorState.OFF.name, any()) }
    }

    @Test
    fun `handleTimeout reverts PENDING_STOP to ON after 30s`() = runTest {
        val expired = System.currentTimeMillis() - 31_000
        coEvery { motorStateDao.get() } returns entity(
            MotorState.PENDING_STOP, pendingSince = expired
        )

        stateMachine.handleTimeout()

        coVerify { motorStateDao.updateState(MotorState.ON.name, any()) }
    }

    private fun entity(
        state: MotorState,
        pendingSince: Long? = null
    ) = MotorStateEntity(
        state = state.name,
        pendingCommand = if (state.isPending) "CMD" else null,
        pendingSince = pendingSince
    )
}
