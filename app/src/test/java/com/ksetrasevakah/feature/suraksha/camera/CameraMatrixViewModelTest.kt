package com.ksetrasevakah.feature.suraksha.camera

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.camera.model.CameraMatrixUiEvent
import com.ksetrasevakah.feature.suraksha.domain.model.CameraConfig
import com.ksetrasevakah.feature.suraksha.domain.model.CameraMode
import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class CameraMatrixViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var cameraConfigRepository: CameraConfigRepository

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cameraConfigRepository = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        cameras: List<CameraConfig> = emptyList()
    ): CameraMatrixViewModel {
        every { cameraConfigRepository.observeAllCameras() } returns
            flowOf(Result.Success(cameras))
        return CameraMatrixViewModel(cameraConfigRepository)
    }

    @Test
    fun `initial state loads cameras successfully`() = runTest {
        val now = System.currentTimeMillis()
        val cameras = listOf(
            CameraConfig(id = 1L, cameraName = "Front Gate", mode = CameraMode.ACTIVE, lastSeen = now, createdAt = now, zone = "A"),
            CameraConfig(id = 2L, cameraName = "Back Yard", mode = CameraMode.ACTIVE, lastSeen = now - 20 * 60 * 1000L, createdAt = now, zone = "B")
        )
        val vm = createViewModel(cameras)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.cameras.size)
        assertEquals(1, state.onlineCount)
        assertEquals(1, state.offlineCount)
    }

    @Test
    fun `error from repository is reflected in UI state`() = runTest {
        every { cameraConfigRepository.observeAllCameras() } returns
            flowOf(Result.Error("Camera offline"))

        val vm = CameraMatrixViewModel(cameraConfigRepository)
        advanceUntilIdle()

        assertEquals("Camera offline", vm.uiState.value.error)
    }

    @Test
    fun `change camera mode calls repository`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        coEvery { cameraConfigRepository.updateCameraMode(1L, CameraMode.SILENT) } returns
            Result.Success(Unit)

        vm.onEvent(CameraMatrixUiEvent.ChangeCameraMode(1L, CameraMode.SILENT))
        advanceUntilIdle()

        coVerify { cameraConfigRepository.updateCameraMode(1L, CameraMode.SILENT) }
    }

    @Test
    fun `retry reloads cameras`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onEvent(CameraMatrixUiEvent.Retry)
        advanceUntilIdle()

        // observeAllCameras called at least twice: init + retry
        coVerify(atLeast = 2) { cameraConfigRepository.observeAllCameras() }
    }
}
