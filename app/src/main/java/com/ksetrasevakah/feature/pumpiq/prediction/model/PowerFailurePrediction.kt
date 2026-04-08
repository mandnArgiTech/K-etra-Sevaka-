package com.ksetrasevakah.feature.pumpiq.prediction.model

import com.ksetrasevakah.designsystem.model.RiskLevel

data class PowerFailurePrediction(
    val predictedTimeHour: Int? = null,
    val predictedTimeMinute: Int? = null,
    val predictedVoltage: Float? = null,
    val confidence: Float = 0f,
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val historicalDipCount: Int = 0,
    val totalDays: Int = 0,
    val insufficientData: Boolean = false
)
