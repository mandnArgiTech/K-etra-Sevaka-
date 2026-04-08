package com.ksetrasevakah.feature.suraksha.dashboard.model

import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel

data class SurakshaDashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val threatCounts: Map<ThreatLevel, Int> = emptyMap(),
    val ledgerEvents: List<SecurityEvent> = emptyList(),
    val briefing: String = "",
    val heatmapData: List<HeatmapEntry> = emptyList(),
    val unacknowledgedCount: Int = 0
)

data class HeatmapEntry(
    val hour: Int,
    val dayOfWeek: Int,
    val count: Int
)
