package com.ksetrasevakah.feature.pumpiq.prediction.model

import com.ksetrasevakah.designsystem.model.RiskLevel

data class ForgotOffPrediction(
    val riskPercent: Int = 0,
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val historicalForgotCount: Int = 0,
    val totalDays: Int = 0,
    val insufficientData: Boolean = false
)
