package com.ksetrasevakah.feature.pumpiq.prediction.model

import com.ksetrasevakah.designsystem.model.RiskLevel

data class FaultPrediction(
    val nextFaultHours: Float? = null,
    val mostLikelyType: String? = null,
    val riskTimeline: List<RiskTimelineEntry> = emptyList(),
    val riskLevel: RiskLevel = RiskLevel.LOW,
    val confidence: Float = 0f,
    val insufficientData: Boolean = false
)
