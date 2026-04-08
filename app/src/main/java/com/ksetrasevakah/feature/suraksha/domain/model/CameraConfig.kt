package com.ksetrasevakah.feature.suraksha.domain.model

data class CameraConfig(
    val id: String = "",
    val cameraName: String,
    val name: String = cameraName,
    val mode: CameraMode,
    val lastSeen: Long,
    val createdAt: Long,
    val zone: String = ""
) {
    val isOnline: Boolean
        get() = System.currentTimeMillis() - lastSeen < ONLINE_THRESHOLD_MS

    companion object {
        private const val ONLINE_THRESHOLD_MS = 10 * 60 * 1000L
    }
}
