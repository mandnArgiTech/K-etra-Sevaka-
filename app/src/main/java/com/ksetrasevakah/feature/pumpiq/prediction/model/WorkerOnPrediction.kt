package com.ksetrasevakah.feature.pumpiq.prediction.model

data class WorkerOnPrediction(
    val predictedHour: Int? = null,
    val predictedMinute: Int? = null,
    val confidence: Float = 0f,
    val standardDeviationMinutes: Float = 0f,
    val insufficientData: Boolean = false
)
