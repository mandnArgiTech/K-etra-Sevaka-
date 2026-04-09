package com.ksetrasevakah.feature.pumpiq.domain.usecase

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.MotorStateDao
import com.ksetrasevakah.core.database.entity.MotorStateEntity
import com.ksetrasevakah.core.sms.SmsCommandSender
import com.ksetrasevakah.core.sms.model.SmsCommand
import com.ksetrasevakah.designsystem.model.MotorState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SendSmsCommandUseCaseTest {

    private lateinit var sender: SmsCommandSender
    private lateinit var motorStateDao: MotorStateDao
    private lateinit var useCase: SendSmsCommandUseCase

    @BeforeEach
    fun setup() {
        sender = mockk()
        motorStateDao = mockk(relaxed = true)
        useCase = SendSmsCommandUseCase(sender, motorStateDao)
    }

    @Test
    fun `Start when OFF sends command and transitions to PENDING_START`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.OFF)
        every { sender.sendCommand(SmsCommand.Start) } returns kotlin.Result.success(Unit)

        val result = useCase(SmsCommand.Start)

        assertTrue(result is Result.Success)
        coVerify { motorStateDao.updateState(MotorState.PENDING_START.name, any()) }
    }

    @Test
    fun `Start when ON returns error`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.ON)

        val result = useCase(SmsCommand.Start)

        assertTrue(result is Result.Error)
        assertEquals("Motor is already running", (result as Result.Error).message)
    }

    @Test
    fun `Stop when ON sends command and transitions to PENDING_STOP`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.ON)
        every { sender.sendCommand(SmsCommand.Stop) } returns kotlin.Result.success(Unit)

        val result = useCase(SmsCommand.Stop)

        assertTrue(result is Result.Success)
        coVerify { motorStateDao.updateState(MotorState.PENDING_STOP.name, any()) }
    }

    @Test
    fun `Stop when OFF returns error`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.OFF)

        val result = useCase(SmsCommand.Stop)

        assertTrue(result is Result.Error)
        assertEquals("Motor is already off", (result as Result.Error).message)
    }

    @Test
    fun `any command when PENDING returns error`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.PENDING_START)

        val result = useCase(SmsCommand.Start)

        assertTrue(result is Result.Error)
        assertEquals("Command already in progress", (result as Result.Error).message)
    }

    @Test
    fun `sendCommand failure returns error`() = runTest {
        coEvery { motorStateDao.get() } returns entity(MotorState.OFF)
        every { sender.sendCommand(SmsCommand.Start) } returns
            kotlin.Result.failure(RuntimeException("No signal"))

        val result = useCase(SmsCommand.Start)

        assertTrue(result is Result.Error)
        assertEquals("No signal", (result as Result.Error).message)
    }

    private fun entity(state: MotorState) = MotorStateEntity(state = state.name)
}
