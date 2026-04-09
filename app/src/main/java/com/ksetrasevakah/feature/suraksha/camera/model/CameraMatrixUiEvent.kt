package com.ksetrasevakah.feature.suraksha.camera.model

import com.ksetrasevakah.feature.suraksha.domain.model.CameraMode

sealed interface CameraMatrixUiEvent {
    data class ChangeCameraMode(val cameraId: Long, val mode: CameraMode) : CameraMatrixUiEvent
    data object Refresh : CameraMatrixUiEvent
    data object NavigateBack : CameraMatrixUiEvent
    data object Retry : CameraMatrixUiEvent
}
