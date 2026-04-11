package com.ksetrasevakah.feature.setup

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.ai.ModelDownloader
import com.ksetrasevakah.core.ai.SmsTelemetryProcessor
import com.ksetrasevakah.core.ai.model.DownloadProgress
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import com.ksetrasevakah.core.sms.SmsHistoryReader
import com.ksetrasevakah.core.vectorstore.EmbeddingDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.ksetrasevakah.core.network.HuggingFaceReadTokenProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupUiState(
    val phase: SetupPhase = SetupPhase.Permissions,
    val statusMessage: String = "",
    val downloadProgress: Float = 0f,
    /** True when byte totals are unknown (show indeterminate bar). */
    val downloadIndeterminate: Boolean = false,
    val isWorking: Boolean = false,
    val error: String? = null,
    val canContinue: Boolean = false
)

enum class SetupPhase {
    Permissions,
    /** SMS inbox + Tapo notification checks before any model download. */
    FundamentalsDiagnostics,
    DownloadModels,
    IngestSms,
    Done
}

@HiltViewModel
class SetupViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val modelDownloader: ModelDownloader,
    private val embeddingDownloader: EmbeddingDownloader,
    private val smsHistoryReader: SmsHistoryReader,
    private val smsTelemetryProcessor: SmsTelemetryProcessor,
    private val appPreferences: AppPreferencesRepository,
    private val huggingFaceReadTokenProvider: HuggingFaceReadTokenProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    /** Saved HF token (for prefilling the setup field). */
    val huggingFaceReadToken: StateFlow<String> = appPreferences.huggingFaceReadToken
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun hasRequiredPermissions(): Boolean {
        val readSms = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
        val recv = ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED
        return readSms && recv
    }

    fun onPermissionsGranted() {
        _uiState.update {
            it.copy(
                phase = SetupPhase.FundamentalsDiagnostics,
                error = null,
                canContinue = false
            )
        }
    }

    /** User finished SMS / notification verification; large model downloads may start next. */
    fun continueAfterFundamentals() {
        _uiState.update {
            it.copy(phase = SetupPhase.DownloadModels, error = null, canContinue = true)
        }
    }

    /**
     * @param huggingFaceTokenFromUi if non-blank, saved to preferences before downloads
     * (Gemma is gated on Hugging Face and needs a read token on the device).
     */
    fun startDownloadsAndIngestion(huggingFaceTokenFromUi: String = "") {
        viewModelScope.launch {
            if (huggingFaceTokenFromUi.isNotBlank()) {
                appPreferences.setHuggingFaceReadToken(huggingFaceTokenFromUi)
            }
            huggingFaceReadTokenProvider.refreshFromStorage()

            _uiState.update {
                it.copy(
                    isWorking = true,
                    error = null,
                    downloadProgress = 0f,
                    downloadIndeterminate = false,
                    statusMessage = "Downloading embedding assets…"
                )
            }

            when (
                val emb = embeddingDownloader.ensureEmbeddingAssets { fileIndex, fileCount, read, total ->
                    val overall = embeddingSegmentProgress(fileIndex, fileCount, read, total)
                    val indeterminate = total == null && read > 0L
                    _uiState.update { s ->
                        s.copy(
                            downloadProgress = overall,
                            downloadIndeterminate = indeterminate
                        )
                    }
                }
            ) {
                is Result.Error -> {
                    _uiState.update { s ->
                        s.copy(isWorking = false, error = emb.message, downloadIndeterminate = false)
                    }
                    return@launch
                }
                else -> Unit
            }

            _uiState.update {
                it.copy(downloadProgress = 0.25f, downloadIndeterminate = false)
            }

            _uiState.update { it.copy(statusMessage = "Downloading Gemma 3 1B chat model…") }
            when (
                val r = modelDownloader.downloadModelIfNeeded(Constants.ORCHESTRATOR_MODEL_ID) { prog ->
                    applyModelProgress(modelIndex = 0, prog = prog)
                }
            ) {
                is Result.Error -> {
                    _uiState.update { s ->
                        s.copy(isWorking = false, error = r.message, downloadIndeterminate = false)
                    }
                    return@launch
                }
                else -> Unit
            }

            appPreferences.setModelsDownloaded(true)

            _uiState.update {
                it.copy(
                    phase = SetupPhase.IngestSms,
                    statusMessage = "Ingesting SMS history…",
                    downloadProgress = 0f,
                    downloadIndeterminate = true
                )
            }

            val messages = try {
                smsHistoryReader.readPanelMessages(500)
            } catch (e: Exception) {
                _uiState.update { s ->
                    s.copy(
                        isWorking = false,
                        error = "SMS read failed: ${e.message}",
                        phase = SetupPhase.Done,
                        canContinue = true,
                        downloadIndeterminate = false,
                        downloadProgress = 1f
                    )
                }
                return@launch
            }

            if (messages.isEmpty()) {
                _uiState.update {
                    it.copy(
                        phase = SetupPhase.Done,
                        isWorking = false,
                        statusMessage = "Setup complete.",
                        canContinue = true,
                        error = null,
                        downloadProgress = 1f,
                        downloadIndeterminate = false
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(downloadIndeterminate = false, downloadProgress = 0.001f)
            }

            messages.forEachIndexed { index, sms ->
                smsTelemetryProcessor.process(sms.body, sms.timestamp)
                val done = index + 1
                val frac = done.toFloat() / messages.size
                _uiState.update { s ->
                    s.copy(
                        statusMessage = "Ingesting SMS $done/${messages.size}…",
                        downloadProgress = frac
                    )
                }
            }

            _uiState.update {
                it.copy(
                    phase = SetupPhase.Done,
                    isWorking = false,
                    statusMessage = "Setup complete.",
                    canContinue = true,
                    error = null,
                    downloadProgress = 1f,
                    downloadIndeterminate = false
                )
            }
        }
    }

    /** Maps embedding downloads into 0 .. 0.25 of the overall bar. */
    private fun embeddingSegmentProgress(
        fileIndex: Int,
        fileCount: Int,
        bytesRead: Long,
        contentLength: Long?
    ): Float {
        if (fileCount <= 0) return 0f
        val local = when {
            contentLength != null && contentLength > 0 ->
                (bytesRead.toDouble() / contentLength.toDouble()).toFloat().coerceIn(0f, 1f)
            bytesRead > 0L -> 0f
            else -> 0f
        }
        return ((fileIndex + local) / fileCount) * 0.25f
    }

    /** Maps the single chat-model download into 0.25 .. 1.0. */
    private fun applyModelProgress(modelIndex: Int, prog: DownloadProgress) {
        val start = 0.25f + modelIndex * 0.75f
        val span = 0.75f
        val indeterminate = prog.phase == DownloadProgress.Phase.Downloading &&
            prog.contentLength == null
        val frac = when (prog.phase) {
            DownloadProgress.Phase.Complete -> 1f
            DownloadProgress.Phase.Failed -> 0f
            DownloadProgress.Phase.Downloading -> {
                if (prog.contentLength != null && prog.contentLength > 0) {
                    prog.fraction
                } else {
                    0f
                }
            }
        }
        _uiState.update { s ->
            s.copy(
                downloadProgress = (start + frac * span).coerceIn(0f, 1f),
                downloadIndeterminate = indeterminate
            )
        }
    }
}
