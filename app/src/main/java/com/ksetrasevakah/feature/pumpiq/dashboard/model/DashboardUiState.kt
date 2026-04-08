package com.ksetrasevakah.feature.pumpiq.dashboard.model

import com.ksetrasevakah.designsystem.model.MotorState
import com.ksetrasevakah.designsystem.model.RiskLevel

data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val motorState: MotorState = MotorState.OFF,
    val phaseR: Float? = null,
    val phaseY: Float? = null,
    val phaseB: Float? = null,
    val voltage: Float? = null,
    val temperature: Float? = null,
    val sessionStartTime: Long? = null,
    val predictions: PredictionsUiState = PredictionsUiState(),
    val dailySummary: String = "",
    val activeChart: ChartTab = ChartTab.PHASE
)

data class PredictionsUiState(
    val powerFailureTime: String? = null,
    val powerFailureRisk: RiskLevel = RiskLevel.LOW,
    val nextFaultHours: String? = null,
    val nextFaultRisk: RiskLevel = RiskLevel.LOW,
    val workerOnTime: String? = null,
    val workerOnRisk: RiskLevel = RiskLevel.LOW,
    val forgotOffPercent: String? = null,
    val forgotOffRisk: RiskLevel = RiskLevel.LOW,
    val hasInsufficientData: Boolean = false
)

enum class ChartTab { PHASE, UPTIME, FAULTS, WORKER, POWER }
