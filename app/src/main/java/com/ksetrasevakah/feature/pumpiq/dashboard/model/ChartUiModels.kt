package com.ksetrasevakah.feature.pumpiq.dashboard.model

/** Grid uptime chart row (no Compose dependency). */
data class GridReliabilityRow(
    val label: String,
    val uptimeHours: Float,
    val downtimeHours: Float
)
