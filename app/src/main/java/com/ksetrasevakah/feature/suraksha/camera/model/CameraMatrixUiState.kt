package com.ksetrasevakah.feature.suraksha.camera.model

import com.ksetrasevakah.feature.suraksha.domain.model.CameraConfig

data class CameraMatrixUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val cameras: List<CameraConfig> = emptyList(),
    val onlineCount: Int = 0,
    val offlineCount: Int = 0
)
