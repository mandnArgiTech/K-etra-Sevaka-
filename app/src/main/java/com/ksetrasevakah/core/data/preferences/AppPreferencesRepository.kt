package com.ksetrasevakah.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ksetrasevakah.core.common.Constants
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ksetra_prefs")

@Singleton
class AppPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore: DataStore<Preferences> get() = context.dataStore

    val watchdogEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[WATCHDOG_ENABLED_KEY] ?: true
    }

    val isFirstRunComplete: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[FIRST_RUN_COMPLETE_KEY] ?: false
    }

    val modelsDownloaded: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[MODELS_DOWNLOADED_KEY] ?: false
    }

    /** Wall-clock time when SMS telemetry was last written to SQLite (any path). */
    val lastTelemetryIngestAt: Flow<Long?> = dataStore.data.map { prefs ->
        prefs[LAST_TELEMETRY_INGEST_AT_KEY]
    }

    /** Phone number (digits only) from which panel SMS are read/matched. */
    val panelNumber: Flow<String> = dataStore.data.map { prefs ->
        prefs[PANEL_NUMBER_KEY] ?: Constants.TARO_PANEL_NUMBER
    }

    /** Package name of the app whose notifications the listener intercepts. */
    val tapoPackageName: Flow<String> = dataStore.data.map { prefs ->
        prefs[TAPO_PACKAGE_KEY] ?: Constants.TAPO_PACKAGE_NAME
    }

    /** Last SMS ingest wall time (ms) for boot diagnostics (check 6). */
    val lastIngestTotalMs: Flow<Long?> = dataStore.data.map { prefs ->
        prefs[LAST_INGEST_TOTAL_MS_KEY]
    }

    /** Regex/structured extraction + DB insert phase (ms); legacy key name `llm` from LLM era. */
    val lastIngestLlmMs: Flow<Long?> = dataStore.data.map { prefs ->
        prefs[LAST_INGEST_LLM_MS_KEY]
    }

    /** Last RAG embedding ingest duration (ms). */
    val lastIngestRagMs: Flow<Long?> = dataStore.data.map { prefs ->
        prefs[LAST_INGEST_RAG_MS_KEY]
    }

    /** Optional Hugging Face read token for gated model downloads (e.g. Gemma). */
    val huggingFaceReadToken: Flow<String> = dataStore.data.map { prefs ->
        prefs[HUGGINGFACE_READ_TOKEN_KEY] ?: ""
    }

    suspend fun isWatchdogEnabled(): Boolean =
        watchdogEnabled.first()

    suspend fun setWatchdogEnabled(enabled: Boolean) {
        dataStore.edit { it[WATCHDOG_ENABLED_KEY] = enabled }
    }

    suspend fun setFirstRunComplete(complete: Boolean) {
        dataStore.edit { it[FIRST_RUN_COMPLETE_KEY] = complete }
    }

    suspend fun setModelsDownloaded(downloaded: Boolean) {
        dataStore.edit { it[MODELS_DOWNLOADED_KEY] = downloaded }
    }

    suspend fun isFirstRunCompleteOnce(): Boolean = isFirstRunComplete.first()

    suspend fun areModelsDownloadedOnce(): Boolean = modelsDownloaded.first()

    suspend fun getLastTelemetryIngestAtOnce(): Long? =
        lastTelemetryIngestAt.first()

    suspend fun setLastTelemetryIngestAt(whenMs: Long) {
        dataStore.edit { it[LAST_TELEMETRY_INGEST_AT_KEY] = whenMs }
    }

    suspend fun getPanelNumber(): String = panelNumber.first()

    suspend fun setPanelNumber(number: String) {
        dataStore.edit { it[PANEL_NUMBER_KEY] = number }
    }

    suspend fun getTapoPackageName(): String = tapoPackageName.first()

    suspend fun setTapoPackageName(pkg: String) {
        dataStore.edit { it[TAPO_PACKAGE_KEY] = pkg }
    }

    suspend fun getLastIngestTiming(): LastIngestTiming {
        val p = dataStore.data.first()
        return LastIngestTiming(
            totalMs = p[LAST_INGEST_TOTAL_MS_KEY],
            extractMs = p[LAST_INGEST_LLM_MS_KEY],
            ragMs = p[LAST_INGEST_RAG_MS_KEY]
        )
    }

    suspend fun setLastIngestTiming(totalMs: Long, extractMs: Long, ragMs: Long) {
        dataStore.edit {
            it[LAST_INGEST_TOTAL_MS_KEY] = totalMs
            it[LAST_INGEST_LLM_MS_KEY] = extractMs
            it[LAST_INGEST_RAG_MS_KEY] = ragMs
        }
    }

    suspend fun getHuggingFaceReadTokenOnce(): String =
        huggingFaceReadToken.first().trim()

    suspend fun setHuggingFaceReadToken(token: String) {
        val t = token.trim()
        dataStore.edit { prefs ->
            if (t.isEmpty()) {
                prefs.remove(HUGGINGFACE_READ_TOKEN_KEY)
            } else {
                prefs[HUGGINGFACE_READ_TOKEN_KEY] = t
            }
        }
    }

    companion object {
        private val WATCHDOG_ENABLED_KEY = booleanPreferencesKey("watchdog_enabled")
        private val FIRST_RUN_COMPLETE_KEY = booleanPreferencesKey("first_run_complete")
        private val MODELS_DOWNLOADED_KEY = booleanPreferencesKey("models_downloaded")
        private val LAST_TELEMETRY_INGEST_AT_KEY = longPreferencesKey("last_telemetry_ingest_at")
        private val PANEL_NUMBER_KEY = stringPreferencesKey("panel_number")
        private val TAPO_PACKAGE_KEY = stringPreferencesKey("tapo_package")
        private val LAST_INGEST_TOTAL_MS_KEY = longPreferencesKey("last_ingest_total_ms")
        private val LAST_INGEST_LLM_MS_KEY = longPreferencesKey("last_ingest_llm_ms")
        private val LAST_INGEST_RAG_MS_KEY = longPreferencesKey("last_ingest_rag_ms")
        private val HUGGINGFACE_READ_TOKEN_KEY = stringPreferencesKey("huggingface_read_token")
    }
}

data class LastIngestTiming(
    val totalMs: Long?,
    val extractMs: Long?,
    val ragMs: Long?
)
