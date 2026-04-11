package com.ksetrasevakah.feature.diagnostics

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.database.entity.VectorDocumentEntity
import com.ksetrasevakah.core.domain.repository.AppDatabaseOverview
import com.ksetrasevakah.core.domain.repository.DataDiagnosticsRepository
import com.ksetrasevakah.core.sms.SmsBulkIngestor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReingestUiState(
    val isRunning: Boolean = false,
    val progressLabel: String? = null,
    val done: Int = 0,
    val total: Int = 0,
    val finishedMessage: String? = null,
    val error: String? = null
)

data class DataDiagnosticsUiState(
    val isLoading: Boolean = true,
    val overview: AppDatabaseOverview? = null,
    val recentTelemetry: List<TelemetryEntity> = emptyList(),
    val recentVectorChunks: List<VectorDocumentEntity> = emptyList(),
    val error: String? = null,
    val reingest: ReingestUiState = ReingestUiState()
)

@HiltViewModel
class DataDiagnosticsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: DataDiagnosticsRepository,
    private val smsBulkIngestor: SmsBulkIngestor
) : ViewModel() {

    private val _uiState = MutableStateFlow(DataDiagnosticsUiState())
    val uiState: StateFlow<DataDiagnosticsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                loadDiagnosticsSnapshot()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load database stats"
                    )
                }
            }
        }
    }

    fun clearReingestFeedback() {
        _uiState.update {
            it.copy(
                reingest = it.reingest.copy(finishedMessage = null, error = null, progressLabel = null)
            )
        }
    }

    fun startReingestFromInbox(limit: Int = 500) {
        if (!hasRequiredSmsPermissions()) {
            _uiState.update {
                it.copy(
                    reingest = ReingestUiState(
                        error = "SMS permission required. Allow SMS when prompted, or enable it in Android Settings → Apps → Kṣetra Sevakaḥ → Permissions."
                    )
                )
            }
            return
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    reingest = ReingestUiState(
                        isRunning = true,
                        progressLabel = "Reading inbox…",
                        finishedMessage = null,
                        error = null
                    )
                )
            }
            val result = smsBulkIngestor.reingestPanelHistory(limit) { done, total ->
                _uiState.update { s ->
                    s.copy(
                        reingest = s.reingest.copy(
                            progressLabel = "Ingesting $done / $total",
                            done = done,
                            total = total
                        )
                    )
                }
            }
            if (result.isSuccess) {
                val n = result.getOrNull() ?: 0
                _uiState.update { it.copy(isLoading = true) }
                try {
                    loadDiagnosticsSnapshot()
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Refresh after re-ingest failed")
                    }
                }
                _uiState.update { s ->
                    s.copy(
                        reingest = ReingestUiState(
                            isRunning = false,
                            finishedMessage = if (n == 0) {
                                "No panel SMS found in inbox for the configured number."
                            } else {
                                "Re-ingested $n message(s)."
                            }
                        )
                    )
                }
            } else {
                val msg = result.exceptionOrNull()?.message ?: "Re-ingest failed"
                _uiState.update { s ->
                    s.copy(
                        reingest = ReingestUiState(
                            isRunning = false,
                            error = msg
                        )
                    )
                }
            }
        }
    }

    private suspend fun loadDiagnosticsSnapshot() {
        val overview = repository.loadOverview()
        val telem = repository.recentTelemetry(50)
        val vectors = repository.recentVectorChunks(40)
        _uiState.update { s ->
            s.copy(
                isLoading = false,
                overview = overview,
                recentTelemetry = telem,
                recentVectorChunks = vectors,
                error = null,
                reingest = s.reingest
            )
        }
    }

    private fun hasRequiredSmsPermissions(): Boolean {
        val read = ContextCompat.checkSelfPermission(appContext, Manifest.permission.READ_SMS) ==
            PackageManager.PERMISSION_GRANTED
        val recv = ContextCompat.checkSelfPermission(appContext, Manifest.permission.RECEIVE_SMS) ==
            PackageManager.PERMISSION_GRANTED
        return read && recv
    }
}
