package com.ksetrasevakah.feature.pumpiq.dashboard.model

sealed interface DashboardUiEvent {
    data object StartPump : DashboardUiEvent
    data object StopPump : DashboardUiEvent
    data class NavigateToChat(val query: String? = null) : DashboardUiEvent
    data object NavigateBack : DashboardUiEvent
    data class SelectChart(val tab: ChartTab) : DashboardUiEvent
    data object Retry : DashboardUiEvent
    data object ErrorConsumed : DashboardUiEvent
}
