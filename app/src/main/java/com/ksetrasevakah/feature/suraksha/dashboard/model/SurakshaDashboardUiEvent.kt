package com.ksetrasevakah.feature.suraksha.dashboard.model

sealed interface SurakshaDashboardUiEvent {
    data object Refresh : SurakshaDashboardUiEvent
    data class AcknowledgeEvent(val eventId: Long) : SurakshaDashboardUiEvent
    data object NavigateBack : SurakshaDashboardUiEvent
    data object NavigateToCameraMatrix : SurakshaDashboardUiEvent
    data object Retry : SurakshaDashboardUiEvent
}
