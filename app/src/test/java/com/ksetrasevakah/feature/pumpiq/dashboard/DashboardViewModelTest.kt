package com.ksetrasevakah.feature.pumpiq.dashboard

import app.cash.turbine.test
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.core.domain.repository.TelemetryRepository
import com.ksetrasevakah.core.sms.model.SmsCommand
import com.ksetrasevakah.designsystem.model.MotorState
import com.ksetrasevakah.feature.pumpiq.dashboard.model.ChartTab
import com.ksetrasevakah.feature.pumpiq.dashboard.model.DashboardUiEvent
import com.ksetrasevakah.feature.pumpiq.domain.usecase.SendSmsCommandUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var motorStateRepository: MotorStateRepository
    private lateinit var telemetryRepository: TelemetryRepository
    private lateinit var sendSmsCommandUseCase: SendSmsCommandUseCase

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        motorStateRepository = mockk()
        telemetryRepository = mockk()
        sendSmsCommandUseCase = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): DashboardViewModel {
        every { motorStateRepository.observeMotorState() } returns
            flowOf(Result.Success(MotorState.OFF))
        coEvery { motorStateRepository.getSessionDuration() } returns Result.Success(0L)
        every { telemetryRepository.observeRecent(any()) } returns
            flowOf(Result.Success(emptyList()))

        return DashboardViewModel(motorStateRepository, telemetryRepository, sendSmsCommandUseCase)
    }

    @Test
    fun `initial state emits motor OFF after collection`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(MotorState.OFF, state.motorState)
    }

    @Test
    fun `motor ON state updates telemetry values`() = runTest {
        every { motorStateRepository.observeMotorState() } returns
            flowOf(Result.Success(MotorState.ON))
        coEvery { motorStateRepository.getSessionDuration() } returns Result.Success(60_000L)

        val telemetry = TelemetryEntity(
            rawSms = "test", timestamp = System.currentTimeMillis(),
            motorOn = true, phaseR = 3.82f, phaseY = 3.91f, phaseB = 3.78f,
            voltage = 228f, temperature = 42f
        )
        every { telemetryRepository.observeRecent(any()) } returns
            flowOf(Result.Success(listOf(telemetry)))

        val vm = DashboardViewModel(motorStateRepository, telemetryRepository, sendSmsCommandUseCase)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(MotorState.ON, state.motorState)
        assertEquals(3.82f, state.phaseR)
        assertEquals(228f, state.voltage)
    }

    @Test
    fun `SelectChart event updates active chart tab`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onEvent(DashboardUiEvent.SelectChart(ChartTab.FAULTS))
        assertEquals(ChartTab.FAULTS, vm.uiState.value.activeChart)
    }

    @Test
    fun `error from motor state repository is reflected in UI state`() = runTest {
        every { motorStateRepository.observeMotorState() } returns
            flowOf(Result.Error("Connection failed"))
        coEvery { motorStateRepository.getSessionDuration() } returns Result.Success(0L)
        every { telemetryRepository.observeRecent(any()) } returns
            flowOf(Result.Success(emptyList()))

        val vm = DashboardViewModel(motorStateRepository, telemetryRepository, sendSmsCommandUseCase)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals("Connection failed", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `start pump error sets commandFeedback without clearing on subsequent motor Success`() = runTest {
        val motorFlow = MutableSharedFlow<Result<MotorState>>(replay = 1)
        motorFlow.tryEmit(Result.Success(MotorState.OFF))
        every { motorStateRepository.observeMotorState() } returns motorFlow
        coEvery { motorStateRepository.getSessionDuration() } returns Result.Success(0L)
        every { telemetryRepository.observeRecent(any()) } returns
            flowOf(Result.Success(emptyList()))
        coEvery { sendSmsCommandUseCase(SmsCommand.Start) } returns Result.Error("Motor is already running")

        val vm = DashboardViewModel(motorStateRepository, telemetryRepository, sendSmsCommandUseCase)
        advanceUntilIdle()

        vm.onEvent(DashboardUiEvent.StartPump)
        advanceUntilIdle()

        assertEquals("Motor is already running", vm.uiState.value.commandFeedback)
        assertEquals(MotorState.OFF, vm.uiState.value.motorState)

        motorFlow.emit(Result.Success(MotorState.OFF))
        advanceUntilIdle()

        assertEquals("Motor is already running", vm.uiState.value.commandFeedback)
        vm.onEvent(DashboardUiEvent.ErrorConsumed)
        assertEquals(null, vm.uiState.value.commandFeedback)
        coVerify(atLeast = 1) { sendSmsCommandUseCase(SmsCommand.Start) }
    }
}
