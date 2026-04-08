package com.ksetrasevakah.feature.suraksha.domain.model

data class SecurityBriefing(
    val id: Long = 0,
    val generatedAt: Long,
    val periodStart: Long,
    val periodEnd: Long,
    val summary: String,
    val totalEvents: Int,
    val criticalCount: Int,
    val highCount: Int
)
