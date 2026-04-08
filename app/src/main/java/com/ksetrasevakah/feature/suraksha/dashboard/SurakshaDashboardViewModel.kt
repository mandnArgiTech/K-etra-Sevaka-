package com.ksetrasevakah.feature.suraksha.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.dashboard.model.HeatmapEntry
import com.ksetrasevakah.feature.suraksha.dashboard.model.SurakshaDashboardUiEvent
import com.ksetrasevakah.feature.suraksha.dashboard.model.SurakshaDashboardUiState
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import com.ksetrasevakah.feature.suraksha.prediction.SecurityBriefingGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SurakshaDashboardViewModel @Inject constructor(
    private val securityEventRepository: SecurityEventRepository,
    private val briefingGenerator: SecurityBriefingGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow(SurakshaDashboardUiState())
    val uiState: StateFlow<SurakshaDashboardUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationTarget>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        loadDashboard()
    }

    fun onEvent(event: SurakshaDashboardUiEvent) {
        when (event) {
            is SurakshaDashboardUiEvent.Refresh -> loadDashboard()
            is SurakshaDashboardUiEvent.AcknowledgeEvent -> acknowledgeEvent(event.eventId)
            is SurakshaDashboardUiEvent.NavigateBack -> viewModelScope.launch {
                _navigationEvents.emit(NavigationTarget.Back)
            }
            is SurakshaDashboardUiEvent.NavigateToCameraMatrix -> viewModelScope.launch {
                _navigationEvents.emit(NavigationTarget.CameraMatrix)
            }
            is SurakshaDashboardUiEvent.Retry -> {
                _uiState.update { it.copy(isLoading = true, error = null) }
                loadDashboard()
            }
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            loadThreatCounts()
            loadUnacknowledgedCount()
            observeEvents()
            loadBriefing()
        }
    }

    private fun loadThreatCounts() {
        viewModelScope.launch {
            when (val result = securityEventRepository.getThreatCounts()) {
                is Result.Success -> _uiState.update { it.copy(threatCounts = result.data) }
                is Result.Error -> _uiState.update { it.copy(error = result.message) }
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    private fun loadUnacknowledgedCount() {
        viewModelScope.launch {
            when (val result = securityEventRepository.getUnacknowledgedCount()) {
                is Result.Success -> _uiState.update { it.copy(unacknowledgedCount = result.data) }
                is Result.Error -> { /* non-fatal */ }
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            securityEventRepository.observeEvents(limit = 50).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val events = result.data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                ledgerEvents = events,
                                heatmapData = buildHeatmap(events)
                            )
                        }
                    }
                    is Result.Error -> _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun loadBriefing() {
        viewModelScope.launch {
            when (val result = briefingGenerator.generate()) {
                is Result.Success -> _uiState.update { it.copy(briefing = result.data) }
                is Result.Error -> { /* briefing is optional */ }
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    private fun acknowledgeEvent(eventId: Long) {
        viewModelScope.launch {
            when (securityEventRepository.acknowledgeEvent(eventId)) {
                is Result.Success -> loadUnacknowledgedCount()
                is Result.Error -> { /* silently handled */ }
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    private fun buildHeatmap(events: List<SecurityEvent>): List<HeatmapEntry> {
        val calendar = Calendar.getInstance()
        return events.groupBy { event ->
            calendar.timeInMillis = event.timestamp
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val day = calendar.get(Calendar.DAY_OF_WEEK)
            hour to day
        }.map { (key, group) ->
            HeatmapEntry(hour = key.first, dayOfWeek = key.second, count = group.size)
        }
    }

    sealed interface NavigationTarget {
        data object Back : NavigationTarget
        data object CameraMatrix : NavigationTarget
    }
}
