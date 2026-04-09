package com.ksetrasevakah.feature.suraksha.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.camera.model.CameraMatrixUiEvent
import com.ksetrasevakah.feature.suraksha.camera.model.CameraMatrixUiState
import com.ksetrasevakah.feature.suraksha.domain.model.CameraMode
import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraMatrixViewModel @Inject constructor(
    private val cameraConfigRepository: CameraConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraMatrixUiState())
    val uiState: StateFlow<CameraMatrixUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationTarget>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        observeCameras()
    }

    fun onEvent(event: CameraMatrixUiEvent) {
        when (event) {
            is CameraMatrixUiEvent.ChangeCameraMode -> changeCameraMode(event.cameraId, event.mode)
            is CameraMatrixUiEvent.Refresh -> observeCameras()
            is CameraMatrixUiEvent.NavigateBack -> viewModelScope.launch {
                _navigationEvents.emit(NavigationTarget.Back)
            }
            is CameraMatrixUiEvent.Retry -> {
                _uiState.update { it.copy(isLoading = true, error = null) }
                observeCameras()
            }
        }
    }

    private fun observeCameras() {
        viewModelScope.launch {
            cameraConfigRepository.observeAllCameras().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val cameras = result.data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                cameras = cameras,
                                onlineCount = cameras.count { cam -> cam.isOnline },
                                offlineCount = cameras.count { cam -> !cam.isOnline }
                            )
                        }
                    }
                    is Result.Error -> _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun changeCameraMode(cameraId: Long, mode: CameraMode) {
        viewModelScope.launch {
            when (val result = cameraConfigRepository.updateCameraMode(cameraId, mode)) {
                is Result.Error -> _uiState.update { it.copy(error = result.message) }
                else -> { /* camera list will update via observeAllCameras flow */ }
            }
        }
    }

    sealed interface NavigationTarget {
        data object Back : NavigationTarget
    }
}
