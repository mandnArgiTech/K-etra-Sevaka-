package com.ksetrasevakah.feature.pumpiq.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.domain.repository.FaultRepository
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.core.domain.repository.TelemetryRepository
import com.ksetrasevakah.core.sms.model.SmsCommand
import com.ksetrasevakah.designsystem.model.MotorState
import com.ksetrasevakah.designsystem.model.RiskLevel
import com.ksetrasevakah.feature.pumpiq.dashboard.model.ChartTab
import com.ksetrasevakah.feature.pumpiq.dashboard.model.DashboardUiEvent
import com.ksetrasevakah.feature.pumpiq.dashboard.model.DashboardUiState
import com.ksetrasevakah.feature.pumpiq.dashboard.model.GridReliabilityRow
import com.ksetrasevakah.feature.pumpiq.dashboard.model.PredictionsUiState
import com.ksetrasevakah.feature.pumpiq.domain.usecase.SendSmsCommandUseCase
import com.ksetrasevakah.feature.pumpiq.prediction.PredictionEngine
import com.ksetrasevakah.feature.pumpiq.prediction.PredictionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val motorStateRepository: MotorStateRepository,
    private val telemetryRepository: TelemetryRepository,
    private val sendSmsCommandUseCase: SendSmsCommandUseCase,
    private val predictionEngine: PredictionEngine,
    private val faultRepository: FaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationTarget>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    /** Last motor state from repository; used to revert optimistic mistakes on SMS errors. */
    private var lastStableMotorState: MotorState = MotorState.OFF

    private var motorObserveJob: Job? = null
    private var telemetryObserveJob: Job? = null
    private var predictionsJob: Job? = null

    init {
        observeMotorState()
        observeTelemetry()
        refreshPredictionsAndFaults()
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
                refreshPredictionsAndFaults()
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

    private fun refreshPredictionsAndFaults() {
        predictionsJob?.cancel()
        predictionsJob = viewModelScope.launch {
            runCatching { predictionEngine.predictAll(forceRefresh = false) }
                .onSuccess { pr ->
                    _uiState.update { it.copy(predictions = mapPredictions(pr)) }
                }
            when (val dist = faultRepository.getFaultDistribution(days = 30)) {
                is Result.Success -> {
                    val pairs = dist.data.map { fc -> fc.faultType to fc.count.toFloat() }
                    _uiState.update { it.copy(faultDistribution = pairs) }
                }
                else -> Unit
            }
        }
    }

    private fun mapPredictions(pr: PredictionResult): PredictionsUiState {
        val pf = pr.powerFailure
        val powerTime = if (pf.predictedTimeHour != null && pf.predictedTimeMinute != null) {
            "%02d:%02d".format(pf.predictedTimeHour, pf.predictedTimeMinute)
        } else {
            null
        }
        val wo = pr.workerOn
        val workerTime = if (wo.predictedHour != null && wo.predictedMinute != null) {
            "%02d:%02d".format(wo.predictedHour, wo.predictedMinute)
        } else {
            null
        }
        val faultH = pr.fault.nextFaultHours?.let { h ->
            if (h < 1f) "%.0f min".format(h * 60) else "%.1f h".format(h)
        }
        val insufficient = pf.insufficientData &&
            pr.fault.insufficientData &&
            wo.insufficientData &&
            pr.forgotOff.insufficientData
        return PredictionsUiState(
            powerFailureTime = powerTime,
            powerFailureRisk = pf.riskLevel,
            nextFaultHours = faultH,
            nextFaultRisk = pr.fault.riskLevel,
            workerOnTime = workerTime,
            workerOnRisk = riskFromConfidence(wo.confidence),
            forgotOffPercent = "${pr.forgotOff.riskPercent}%",
            forgotOffRisk = pr.forgotOff.riskLevel,
            hasInsufficientData = insufficient
        )
    }

    private fun riskFromConfidence(c: Float): RiskLevel = when {
        c >= 0.65f -> RiskLevel.HIGH
        c >= 0.35f -> RiskLevel.MEDIUM
        else -> RiskLevel.LOW
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
                        } else {
                            null
                        }

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
                        val list = result.data
                        val sorted = list.sortedBy { it.timestamp }
                        val maxPoints = 48
                        val r = sorted.mapNotNull { it.phaseR }.takeLast(maxPoints)
                        val y = sorted.mapNotNull { it.phaseY }.takeLast(maxPoints)
                        val b = sorted.mapNotNull { it.phaseB }.takeLast(maxPoints)
                        val volts = sorted.mapNotNull { it.voltage }.takeLast(maxPoints)
                        val latest = sorted.lastOrNull()
                        _uiState.update {
                            it.copy(
                                phaseR = latest?.phaseR,
                                phaseY = latest?.phaseY,
                                phaseB = latest?.phaseB,
                                voltage = latest?.voltage,
                                temperature = latest?.temperature,
                                dailySummary = latest?.narrative ?: "",
                                phaseSeriesR = r,
                                phaseSeriesY = y,
                                phaseSeriesB = b,
                                powerVoltageHistory = volts,
                                gridReliabilityRows = buildGridReliabilityRows(sorted)
                            )
                        }
                    }
                    is Result.Error -> { /* telemetry errors are non-fatal */ }
                    is Result.Loading -> { /* no-op */ }
                }
            }
        }
    }

    private fun buildGridReliabilityRows(telemetry: List<TelemetryEntity>): List<GridReliabilityRow> {
        if (telemetry.isEmpty()) return emptyList()
        val zone = ZoneId.systemDefault()
        val byDay = telemetry.groupBy { row ->
            Instant.ofEpochMilli(row.timestamp).atZone(zone).toLocalDate()
        }.toList().sortedBy { it.first }.takeLast(7)
        return byDay.map { (date, rows) ->
            val ok = rows.count { (it.voltage ?: 0f) in 195f..255f }
            val ratio = ok.toFloat() / rows.size.coerceAtLeast(1)
            GridReliabilityRow(
                label = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                uptimeHours = 24f * ratio,
                downtimeHours = 24f * (1f - ratio).coerceIn(0f, 1f)
            )
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
