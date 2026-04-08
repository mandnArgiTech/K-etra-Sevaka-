package com.ksetrasevakah.feature.pumpiq.domain.usecase

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.FaultDao
import com.ksetrasevakah.core.database.dao.MotorStateDao
import com.ksetrasevakah.core.database.entity.FaultEntity
import com.ksetrasevakah.designsystem.model.MotorState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProcessSmsConfirmationUseCaseTest {

    private lateinit var motorStateDao: MotorStateDao
    private lateinit var faultDao: FaultDao
    private lateinit var useCase: ProcessSmsConfirmationUseCase

    @BeforeEach
    fun setup() {
        motorStateDao = mockk(relaxed = true)
        faultDao = mockk(relaxed = true)
        useCase = ProcessSmsConfirmationUseCase(motorStateDao, faultDao)
    }

    @Test
    fun `MOTOR ON confirmation transitions to ON`() = runTest {
        val result = useCase("MOTOR ON confirmed")

        assertTrue(result is Result.Success)
        assertEquals(MotorState.ON, (result as Result.Success).data)
        coVerify { motorStateDao.updateState(MotorState.ON.name, any()) }
        coVerify { motorStateDao.setSessionStart(any(), any()) }
    }

    @Test
    fun `MOTOR OFF confirmation transitions to OFF`() = runTest {
        val result = useCase("MOTOR OFF confirmed")

        assertTrue(result is Result.Success)
        assertEquals(MotorState.OFF, (result as Result.Success).data)
        coVerify { motorStateDao.updateState(MotorState.OFF.name, any()) }
        coVerify { motorStateDao.setSessionEnd(any(), any()) }
    }

    @Test
    fun `ALERT creates fault and transitions to OFF`() = runTest {
        coEvery { faultDao.insert(any()) } returns 1L
        val faultSlot = slot<FaultEntity>()

        val result = useCase("ALERT: Dry run detected")

        assertTrue(result is Result.Success)
        assertEquals(MotorState.OFF, (result as Result.Success).data)
        coVerify { faultDao.insert(capture(faultSlot)) }
        assertEquals("SMS_ALERT", faultSlot.captured.faultType)
        assertEquals("Dry run detected", faultSlot.captured.description)
    }

    @Test
    fun `ALERT clears pending command`() = runTest {
        coEvery { faultDao.insert(any()) } returns 1L

        useCase("ALERT: Overload")

        coVerify { motorStateDao.setPendingCommand(null, null, any()) }
    }

    @Test
    fun `unknown SMS body returns error`() = runTest {
        val result = useCase("RANDOM TEXT")

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Unrecognised"))
    }
}
