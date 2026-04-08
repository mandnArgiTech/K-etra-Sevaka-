package com.ksetrasevakah.core.notification.model

import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel

data class CriticalOverlayData(
    val title: String,
    val message: String,
    val cameraName: String,
    val threatLevel: ThreatLevel,
    val timestamp: Long = System.currentTimeMillis()
)
