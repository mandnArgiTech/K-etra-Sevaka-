package com.ksetrasevakah.feature.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.ksetrasevakah.BuildConfig
import com.ksetrasevakah.core.backup.BackupFileInfo
import com.ksetrasevakah.core.backup.BackupManager
import com.ksetrasevakah.core.backup.GoogleSignInManager
import com.ksetrasevakah.core.backup.RestoreManager
import com.ksetrasevakah.core.backup.model.BackupStatus
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.designsystem.model.MotorState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class SettingsUiState(
    val panelNumber: String = Constants.TARO_PANEL_NUMBER,
    val chatModelId: String = Constants.ORCHESTRATOR_MODEL_ID,
    val orchestratorModelDetails: String = "",
    val embeddingModelDetails: String = "",
    val motorState: MotorState = MotorState.OFF,
    val backupStatus: BackupStatus = BackupStatus.Idle,
    val watchdogEnabled: Boolean = true,
    val isLoading: Boolean = true,
    val error: String? = null,
    val driveAccountEmail: String? = null,
    val driveBackups: List<BackupFileInfo> = emptyList(),
    val localBackupNames: List<String> = emptyList(),
    val appVersion: String = BuildConfig.VERSION_NAME,
    val restoreMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val motorStateRepository: MotorStateRepository,
    private val backupManager: BackupManager,
    private val appPreferences: AppPreferencesRepository,
    private val googleSignInManager: GoogleSignInManager,
    private val restoreManager: RestoreManager,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeMotorState()
        observeBackupStatus()
        observePanelNumber()
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    watchdogEnabled = appPreferences.isWatchdogEnabled(),
                    appVersion = BuildConfig.VERSION_NAME,
                    orchestratorModelDetails = modelDetails(Constants.ORCHESTRATOR_MODEL_FILENAME),
                    embeddingModelDetails = embeddingDetails()
                )
            }
            refreshAccountAndLists()
        }
    }

    private fun observePanelNumber() {
        viewModelScope.launch {
            appPreferences.panelNumber.collect { number ->
                _uiState.update { it.copy(panelNumber = number) }
            }
        }
    }

    fun setPanelNumber(number: String) {
        val trimmed = number.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            appPreferences.setPanelNumber(trimmed)
        }
    }

    fun googleSignInIntent(): Intent = googleSignInManager.signInIntent()

    fun onGoogleSignInResult(data: Intent?) {
        if (data == null) return
        viewModelScope.launch {
            try {
                GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)
            } catch (e: ApiException) {
                _uiState.update {
                    it.copy(error = "Google sign-in failed (${e.statusCode})")
                }
                return@launch
            }
            refreshAccountAndLists()
        }
    }

    fun signOutGoogle() {
        viewModelScope.launch {
            googleSignInManager.signOut()
            refreshAccountAndLists()
        }
    }

    fun refreshModelStatus() {
        _uiState.update {
            it.copy(
                orchestratorModelDetails = modelDetails(Constants.ORCHESTRATOR_MODEL_FILENAME),
                embeddingModelDetails = embeddingDetails()
            )
        }
    }

    fun clearRestoreMessage() {
        _uiState.update { it.copy(restoreMessage = null) }
    }

    fun restoreFromLocalBackup(fileName: String) {
        viewModelScope.launch {
            val f = File(File(appContext.filesDir, "backups"), fileName)
            when (val r = restoreManager.restoreFromLocal(f)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(restoreMessage = "Local restore complete. Restart the app to reload data.")
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = r.message, restoreMessage = null) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun restoreFromDrive(fileId: String) {
        viewModelScope.launch {
            val tempDir = File(appContext.cacheDir, "restore_tmp")
            when (val r = restoreManager.restoreFromDrive(fileId, tempDir)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(restoreMessage = "Drive restore complete. Restart the app to reload data.")
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(error = r.message, restoreMessage = null) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun toggleWatchdog(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setWatchdogEnabled(enabled)
            _uiState.update { it.copy(watchdogEnabled = enabled) }
        }
    }

    fun triggerBackup() {
        viewModelScope.launch {
            val backupDir = File(appContext.filesDir, "backups")
            when (val created = backupManager.createBackup(backupDir)) {
                is Result.Success -> {
                    backupManager.uploadBackup(created.data)
                    refreshAccountAndLists()
                }
                is Result.Error, is Result.Loading -> Unit
            }
        }
    }

    private suspend fun refreshAccountAndLists() {
        val email = googleSignInManager.lastSignedInAccount()?.email
        val driveBackups = if (email != null) {
            when (val r = restoreManager.listAvailableBackups()) {
                is Result.Success -> r.data
                else -> emptyList()
            }
        } else {
            emptyList()
        }
        _uiState.update {
            it.copy(
                driveAccountEmail = email,
                driveBackups = driveBackups,
                localBackupNames = listLocalBackups()
            )
        }
    }

    private fun listLocalBackups(): List<String> =
        File(appContext.filesDir, "backups")
            .takeIf { it.isDirectory }
            ?.listFiles()
            ?.filter { it.isFile && it.name.startsWith("ksetra_backup_") && it.name.endsWith(".db") }
            ?.sortedByDescending { it.name }
            ?.map { it.name }
            .orEmpty()

    private fun modelDetails(fileName: String): String {
        val f = File(File(appContext.filesDir, "models"), fileName)
        return if (f.isFile) {
            val mb = f.length() / (1024f * 1024f)
            "On device · %.1f MB".format(mb)
        } else {
            "Not downloaded (finish Setup or download from Hugging Face)"
        }
    }

    private fun embeddingDetails(): String {
        val dir = File(appContext.filesDir, "models")
        val onnx = File(dir, Constants.EMBEDDING_ONNX_FILENAME)
        val vocab = File(dir, Constants.EMBEDDING_VOCAB_FILENAME)
        return if (onnx.isFile && vocab.isFile) {
            "On device"
        } else {
            "Not downloaded"
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
