package com.ksetrasevakah.feature.pumpiq.prediction.model

import com.ksetrasevakah.designsystem.model.RiskLevel

data class RiskTimelineEntry(
    val hoursFromNow: Float,
    val riskPercent: Int,
    val riskLevel: RiskLevel
)
