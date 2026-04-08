package com.ksetrasevakah.feature.settings

import com.ksetrasevakah.core.backup.BackupManager
import com.ksetrasevakah.core.backup.model.BackupStatus
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.designsystem.model.MotorState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var motorStateRepository: MotorStateRepository
    private lateinit var backupManager: BackupManager

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        motorStateRepository = mockk()
        backupManager = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SettingsViewModel {
        every { motorStateRepository.observeMotorState() } returns flowOf(Result.Success(MotorState.OFF))
        every { backupManager.status } returns MutableStateFlow(BackupStatus.Idle)
        return SettingsViewModel(motorStateRepository, backupManager)
    }

    @Test
    fun `initial state shows motor OFF after loading`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(MotorState.OFF, vm.uiState.value.motorState)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `toggle watchdog updates state`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.watchdogEnabled)
        vm.toggleWatchdog(false)
        assertFalse(vm.uiState.value.watchdogEnabled)
    }

    @Test
    fun `panel number matches constant`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals("070936 52065", vm.uiState.value.panelNumber)
    }
}
