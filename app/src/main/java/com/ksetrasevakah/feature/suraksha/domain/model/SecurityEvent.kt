package com.ksetrasevakah.feature.suraksha.domain.model

data class SecurityEvent(
    val id: Long = 0,
    val cameraName: String,
    val cameraId: String = cameraName,
    val eventType: EventType,
    val threatLevel: ThreatLevel,
    val confidence: Float,
    val originTimestamp: Long,
    val receivedTimestamp: Long,
    val timestamp: Long = originTimestamp,
    val hourOfDay: Int,
    val summary: String? = null,
    val description: String = summary ?: "",
    val acknowledged: Boolean = false
)
