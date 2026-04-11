package com.ksetrasevakah.feature.bootdiagnostics

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.ai.MlcLlmEngine
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import com.ksetrasevakah.core.sms.SmsHistoryReader
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.math.max

enum class CheckStatus {
    Running,
    Pass,
    Warn,
    Fail,
    Idle
}

data class DiagnosticCheck(
    val id: String,
    val label: String,
    val status: CheckStatus,
    val detail: String
)

data class BootDiagnosticsUiState(
    val checks: List<DiagnosticCheck> = emptyList(),
    val fastChecksRunning: Boolean = true,
    val benchmarkRunning: Boolean = false,
    val benchmarkError: String? = null
)

@HiltViewModel
class BootDiagnosticsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appPreferences: AppPreferencesRepository,
    private val smsHistoryReader: SmsHistoryReader,
    private val engine: MlcLlmEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BootDiagnosticsUiState(
            checks = placeholderChecks(),
            fastChecksRunning = true
        )
    )
    val uiState: StateFlow<BootDiagnosticsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            runFastChecks()
        }
    }

    fun runOrchestratorBenchmark() {
        if (_uiState.value.benchmarkRunning) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    benchmarkRunning = true,
                    benchmarkError = null,
                    checks = it.checks.map { c ->
                        if (c.id == ID_BENCHMARK) {
                            c.copy(status = CheckStatus.Running, detail = "Loading model & generating…")
                        } else {
                            c
                        }
                    }
                )
            }
            try {
                engine.loadModel(Constants.ORCHESTRATOR_MODEL_ID)
                var chunkCount = 0
                val start = System.currentTimeMillis()
                engine.generate(BENCHMARK_PROMPT, Constants.ORCHESTRATOR_MODEL_ID)
                    .collect { chunk ->
                        if (chunk.isNotBlank()) chunkCount++
                    }
                val elapsedMs = max(1L, System.currentTimeMillis() - start)
                val elapsedSec = elapsedMs / 1000.0
                val perSec = chunkCount / elapsedSec
                val (status, detail) = when {
                    perSec >= 5.0 -> CheckStatus.Pass to
                        "${chunkCount} chunks in ${"%.1f".format(elapsedSec)}s ≈ ${"%.1f".format(perSec)} chunks/s (>5 pass)"
                    perSec >= 2.0 -> CheckStatus.Warn to
                        "${chunkCount} chunks in ${"%.1f".format(elapsedSec)}s ≈ ${"%.1f".format(perSec)} chunks/s (2–5 warn)"
                    else -> CheckStatus.Fail to
                        "${chunkCount} chunks in ${"%.1f".format(elapsedSec)}s ≈ ${"%.1f".format(perSec)} chunks/s (<2 fail)"
                }
                updateCheck(ID_BENCHMARK) {
                    it.copy(status = status, detail = detail)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(benchmarkError = e.message ?: "Benchmark failed") }
                updateCheck(ID_BENCHMARK) {
                    it.copy(
                        status = CheckStatus.Fail,
                        detail = "Error: ${e.message ?: "unknown"}"
                    )
                }
            } finally {
                _uiState.update { it.copy(benchmarkRunning = false) }
            }
        }
    }

    private suspend fun runFastChecks() {
        _uiState.update {
            it.copy(
                fastChecksRunning = true,
                checks = placeholderChecks()
            )
        }
        val results = supervisorScope {
            val c1 = async { checkSmsPermission() }
            val c2 = async { checkSmsData() }
            val c3 = async { checkNotificationListener() }
            val c4 = async { checkChatModelOnDisk() }
            val c5 = async { checkEmbeddingAssets() }
            val c6 = async { checkIngestSpeed() }
            listOf(c1, c2, c3, c4, c5, c6).map { it.await() } + idleBenchmarkRow()
        }
        _uiState.update {
            it.copy(
                checks = results,
                fastChecksRunning = false
            )
        }
    }

    private fun placeholderChecks(): List<DiagnosticCheck> = listOf(
        DiagnosticCheck(ID_SMS_PERM, "SMS permission", CheckStatus.Running, "Checking…"),
        DiagnosticCheck(ID_SMS_DATA, "SMS data (panel)", CheckStatus.Running, "Checking…"),
        DiagnosticCheck(ID_NOTIF, "Notification listener", CheckStatus.Running, "Checking…"),
        DiagnosticCheck(ID_CHAT_MODEL, "Chat model on disk", CheckStatus.Running, "Checking…"),
        DiagnosticCheck(ID_EMBED, "Embedding model (ONNX)", CheckStatus.Running, "Checking…"),
        DiagnosticCheck(ID_INGEST_SPEED, "SMS ingestion speed (last run)", CheckStatus.Running, "Checking…"),
        idleBenchmarkRow()
    )

    private fun idleBenchmarkRow() = DiagnosticCheck(
        id = ID_BENCHMARK,
        label = "Chat model speed (on-demand)",
        status = CheckStatus.Idle,
        detail = "Tap Run Benchmark to load LiteRT GPU model and measure chunks/s."
    )

    private suspend fun checkSmsPermission(): DiagnosticCheck = withContext(Dispatchers.Default) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
        DiagnosticCheck(
            id = ID_SMS_PERM,
            label = "SMS permission",
            status = if (granted) CheckStatus.Pass else CheckStatus.Fail,
            detail = if (granted) "READ_SMS granted" else "READ_SMS not granted"
        )
    }

    private suspend fun checkSmsData(): DiagnosticCheck = withContext(Dispatchers.IO) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            return@withContext DiagnosticCheck(
                id = ID_SMS_DATA,
                label = "SMS data (panel)",
                status = CheckStatus.Fail,
                detail = "Cannot read inbox — permission missing"
            )
        }
        val panel = appPreferences.getPanelNumber()
        val msgs = smsHistoryReader.readMessagesFromNumber(panel, limit = 1)
        when {
            msgs.isNotEmpty() -> DiagnosticCheck(
                id = ID_SMS_DATA,
                label = "SMS data (panel)",
                status = CheckStatus.Pass,
                detail = "≥1 message from $panel"
            )
            else -> DiagnosticCheck(
                id = ID_SMS_DATA,
                label = "SMS data (panel)",
                status = CheckStatus.Warn,
                detail = "0 messages from $panel"
            )
        }
    }

    private suspend fun checkNotificationListener(): DiagnosticCheck = withContext(Dispatchers.Default) {
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: ""
        val enabled = flat.contains(context.packageName)
        DiagnosticCheck(
            id = ID_NOTIF,
            label = "Notification listener",
            status = if (enabled) CheckStatus.Pass else CheckStatus.Fail,
            detail = if (enabled) "Enabled for this app" else "Not enabled — grant in system settings"
        )
    }

    private suspend fun checkChatModelOnDisk(): DiagnosticCheck = withContext(Dispatchers.IO) {
        val f = File(context.filesDir, "models/${Constants.ORCHESTRATOR_MODEL_FILENAME}")
        when {
            !f.exists() -> DiagnosticCheck(
                id = ID_CHAT_MODEL,
                label = "Chat model on disk",
                status = CheckStatus.Fail,
                detail = "Missing ${f.name}. Run setup download. LiteRT uses Backend.GPU() (OpenCL/Adreno) when available."
            )
            f.length() < MIN_MODEL_BYTES -> DiagnosticCheck(
                id = ID_CHAT_MODEL,
                label = "Chat model on disk",
                status = CheckStatus.Warn,
                detail = "File exists but small (${f.length()} B). Expected ≥${MIN_MODEL_BYTES / 1_000_000} MB. GPU path: Backend.GPU()."
            )
            else -> DiagnosticCheck(
                id = ID_CHAT_MODEL,
                label = "Chat model on disk",
                status = CheckStatus.Pass,
                detail = "${f.name} present (${f.length() / 1_000_000} MB). Backend.GPU() requested; actual device may fall back to CPU."
            )
        }
    }

    private suspend fun checkEmbeddingAssets(): DiagnosticCheck = withContext(Dispatchers.IO) {
        val onnx = File(context.filesDir, "embedding/${Constants.EMBEDDING_ONNX_FILENAME}")
        val vocab = File(context.filesDir, "embedding/${Constants.EMBEDDING_VOCAB_FILENAME}")
        val onnxOk = onnx.exists() && onnx.length() >= MIN_ONNX_BYTES
        val vocabOk = vocab.exists() && vocab.length() > MIN_VOCAB_BYTES
        when {
            onnxOk && vocabOk -> DiagnosticCheck(
                id = ID_EMBED,
                label = "Embedding model (ONNX)",
                status = CheckStatus.Pass,
                detail = "ONNX + vocab present"
            )
            !onnx.exists() && !vocab.exists() -> DiagnosticCheck(
                id = ID_EMBED,
                label = "Embedding model (ONNX)",
                status = CheckStatus.Fail,
                detail = "ONNX and vocab missing under files/embedding/"
            )
            else -> DiagnosticCheck(
                id = ID_EMBED,
                label = "Embedding model (ONNX)",
                status = CheckStatus.Fail,
                detail = "ONNX ok=$onnxOk, vocab ok=$vocabOk"
            )
        }
    }

    private suspend fun checkIngestSpeed(): DiagnosticCheck = withContext(Dispatchers.IO) {
        val timing = appPreferences.getLastIngestTiming()
        val total = timing.totalMs
        if (total == null) {
            return@withContext DiagnosticCheck(
                id = ID_INGEST_SPEED,
                label = "SMS ingestion speed (last run)",
                status = CheckStatus.Fail,
                detail = "No timing recorded yet — process an SMS after install"
            )
        }
        val seconds = total / 1000.0
        val (status, detail) = when {
            total < 10_000L -> CheckStatus.Pass to "Last total ${total}ms (${"%.1f".format(seconds)}s) — pass (<10s)"
            total < 30_000L -> CheckStatus.Warn to "Last total ${total}ms (${"%.1f".format(seconds)}s) — warn (10–30s)"
            else -> CheckStatus.Fail to "Last total ${total}ms (${"%.1f".format(seconds)}s) — fail (>30s)"
        }
        val extra = timing.ragMs?.let { r -> timing.extractMs?.let { e -> "; extract=${e}ms, RAG=${r}ms" } } ?: ""
        DiagnosticCheck(
            id = ID_INGEST_SPEED,
            label = "SMS ingestion speed (last run)",
            status = status,
            detail = detail + extra
        )
    }

    private fun updateCheck(id: String, transform: (DiagnosticCheck) -> DiagnosticCheck) {
        _uiState.update { s ->
            s.copy(checks = s.checks.map { if (it.id == id) transform(it) else it })
        }
    }

    companion object {
        const val ID_BENCHMARK = "benchmark"

        private const val ID_SMS_PERM = "sms_perm"
        private const val ID_SMS_DATA = "sms_data"
        private const val ID_NOTIF = "notif_listener"
        private const val ID_CHAT_MODEL = "chat_model"
        private const val ID_EMBED = "embedding"
        private const val ID_INGEST_SPEED = "ingest_speed"

        const val BENCHMARK_PROMPT = "Summarize: Motor started at 6 AM. Phase R=4.2A."

        private const val MIN_MODEL_BYTES = 1_000_000L
        private const val MIN_ONNX_BYTES = 500_000L
        private const val MIN_VOCAB_BYTES = 1_000L
    }
}
