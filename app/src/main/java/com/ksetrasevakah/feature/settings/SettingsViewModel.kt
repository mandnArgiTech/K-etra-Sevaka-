package com.ksetrasevakah.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.backup.BackupManager
import com.ksetrasevakah.core.backup.model.BackupStatus
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.designsystem.model.MotorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val panelNumber: String = Constants.TARO_PANEL_NUMBER,
    val ingestionModelId: String = Constants.INGESTION_MODEL_ID,
    val orchestratorModelId: String = Constants.ORCHESTRATOR_MODEL_ID,
    val motorState: MotorState = MotorState.OFF,
    val backupStatus: BackupStatus = BackupStatus.Idle,
    val watchdogEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val motorStateRepository: MotorStateRepository,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeMotorState()
        observeBackupStatus()
    }

    fun toggleWatchdog(enabled: Boolean) {
        _uiState.update { it.copy(watchdogEnabled = enabled) }
    }

    fun triggerBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(backupStatus = BackupStatus.InProgress()) }
        }
    }

    private fun observeMotorState() {
        viewModelScope.launch {
            motorStateRepository.observeMotorState().collect { result ->
                when (result) {
                    is Result.Success -> _uiState.update {
                        it.copy(motorState = result.data, isLoading = false)
                    }
                    is Result.Error -> _uiState.update {
                        it.copy(error = result.message, isLoading = false)
                    }
                    is Result.Loading -> _uiState.update {
                        it.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun observeBackupStatus() {
        viewModelScope.launch {
            backupManager.status.collect { status ->
                _uiState.update { it.copy(backupStatus = status) }
            }
        }
    }
}
