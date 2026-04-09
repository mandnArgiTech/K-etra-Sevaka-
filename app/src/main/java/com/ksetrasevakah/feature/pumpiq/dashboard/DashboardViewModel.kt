package com.ksetrasevakah.feature.pumpiq.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.core.domain.repository.TelemetryRepository
import com.ksetrasevakah.core.sms.model.SmsCommand
import com.ksetrasevakah.designsystem.model.MotorState
import com.ksetrasevakah.feature.pumpiq.dashboard.model.ChartTab
import com.ksetrasevakah.feature.pumpiq.dashboard.model.DashboardUiEvent
import com.ksetrasevakah.feature.pumpiq.dashboard.model.DashboardUiState
import com.ksetrasevakah.feature.pumpiq.domain.usecase.SendSmsCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val motorStateRepository: MotorStateRepository,
    private val telemetryRepository: TelemetryRepository,
    private val sendSmsCommandUseCase: SendSmsCommandUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationTarget>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    /** Last motor state from repository; used to revert optimistic mistakes on SMS errors. */
    private var lastStableMotorState: MotorState = MotorState.OFF

    private var motorObserveJob: Job? = null
    private var telemetryObserveJob: Job? = null

    init {
        observeMotorState()
        observeTelemetry()
    }

    fun onEvent(event: DashboardUiEvent) {
        when (event) {
            is DashboardUiEvent.StartPump -> startPump()
            is DashboardUiEvent.StopPump -> stopPump()
            is DashboardUiEvent.NavigateToChat -> viewModelScope.launch {
                _navigationEvents.emit(NavigationTarget.Chat(event.query))
            }
            is DashboardUiEvent.NavigateBack -> viewModelScope.launch {
                _navigationEvents.emit(NavigationTarget.Back)
            }
            is DashboardUiEvent.SelectChart -> {
                _uiState.update { it.copy(activeChart = event.tab) }
            }
            is DashboardUiEvent.Retry -> {
                _uiState.update { it.copy(isLoading = true, error = null, commandFeedback = null) }
                observeMotorState()
                observeTelemetry()
            }
            is DashboardUiEvent.ErrorConsumed -> {
                _uiState.update { s ->
                    when {
                        s.commandFeedback != null -> s.copy(commandFeedback = null)
                        s.error != null -> s.copy(error = null)
                        else -> s
                    }
                }
            }
        }
    }

    private fun observeMotorState() {
        motorObserveJob?.cancel()
        motorObserveJob = viewModelScope.launch {
            motorStateRepository.observeMotorState().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val state = result.data
                        lastStableMotorState = state
                        val sessionStart = if (state == MotorState.ON) {
                            when (val dur = motorStateRepository.getSessionDuration()) {
                                is Result.Success -> System.currentTimeMillis() - dur.data
                                else -> null
                            }
                        } else null

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                motorState = state,
                                sessionStartTime = sessionStart
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, error = result.message)
                        }
                    }
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun observeTelemetry() {
        telemetryObserveJob?.cancel()
        telemetryObserveJob = viewModelScope.launch {
            telemetryRepository.observeRecent(days = 1).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val latest = result.data.firstOrNull()
                        _uiState.update {
                            it.copy(
                                phaseR = latest?.phaseR,
                                phaseY = latest?.phaseY,
                                phaseB = latest?.phaseB,
                                voltage = latest?.voltage,
                                temperature = latest?.temperature,
                                dailySummary = latest?.narrative ?: ""
                            )
                        }
                    }
                    is Result.Error -> { /* telemetry errors are non-fatal */ }
                    is Result.Loading -> { /* no-op */ }
                }
            }
        }
    }

    private fun startPump() {
        viewModelScope.launch {
            when (val result = sendSmsCommandUseCase(SmsCommand.Start)) {
                is Result.Error -> {
                    _uiState.update {
                        it.copy(motorState = lastStableMotorState, commandFeedback = result.message)
                    }
                }
                is Result.Success -> {
                    _uiState.update { it.copy(commandFeedback = null) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    private fun stopPump() {
        viewModelScope.launch {
            when (val result = sendSmsCommandUseCase(SmsCommand.Stop)) {
                is Result.Error -> {
                    _uiState.update {
                        it.copy(motorState = lastStableMotorState, commandFeedback = result.message)
                    }
                }
                is Result.Success -> {
                    _uiState.update { it.copy(commandFeedback = null) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    sealed interface NavigationTarget {
        data class Chat(val query: String?) : NavigationTarget
        data object Back : NavigationTarget
    }
}
