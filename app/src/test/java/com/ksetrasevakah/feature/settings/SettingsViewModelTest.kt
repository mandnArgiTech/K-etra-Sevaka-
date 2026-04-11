package com.ksetrasevakah.feature.settings

import android.content.Context
import com.ksetrasevakah.core.backup.BackupManager
import com.ksetrasevakah.core.backup.GoogleSignInManager
import com.ksetrasevakah.core.backup.RestoreManager
import com.ksetrasevakah.core.backup.model.BackupStatus
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.designsystem.model.MotorState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.File
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
    private lateinit var appPreferences: AppPreferencesRepository
    private lateinit var appContext: Context
    private lateinit var googleSignInManager: GoogleSignInManager
    private lateinit var restoreManager: RestoreManager

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        motorStateRepository = mockk()
        backupManager = mockk()
        appPreferences = mockk(relaxed = true)
        coEvery { appPreferences.isWatchdogEnabled() } returns true
        every { appPreferences.panelNumber } returns flowOf("7093652065")
        appContext = mockk(relaxed = true)
        every { appContext.filesDir } returns File(System.getProperty("java.io.tmpdir"), "ksetra-settings-test").apply { mkdirs() }
        every { appContext.cacheDir } returns File(System.getProperty("java.io.tmpdir"), "ksetra-settings-cache").apply { mkdirs() }
        googleSignInManager = mockk(relaxed = true)
        restoreManager = mockk(relaxed = true)
        every { googleSignInManager.lastSignedInAccount() } returns null
        coEvery { restoreManager.listAvailableBackups() } returns Result.Success(emptyList())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SettingsViewModel {
        every { motorStateRepository.observeMotorState() } returns flowOf(Result.Success(MotorState.OFF))
        every { backupManager.status } returns MutableStateFlow(BackupStatus.Idle)
        return SettingsViewModel(
            motorStateRepository,
            backupManager,
            appPreferences,
            googleSignInManager,
            restoreManager,
            appContext
        )
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
        advanceUntilIdle()
        assertFalse(vm.uiState.value.watchdogEnabled)
        coVerify { appPreferences.setWatchdogEnabled(false) }
    }

    @Test
    fun `panel number matches constant`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals("7093652065", vm.uiState.value.panelNumber)
    }
}
