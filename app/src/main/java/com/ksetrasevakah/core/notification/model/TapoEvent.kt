package com.ksetrasevakah.core.notification.model

data class TapoEvent(
    val cameraName: String,
    val eventType: String,
    val timestamp: Long,
    val rawTitle: String,
    val rawText: String
)
