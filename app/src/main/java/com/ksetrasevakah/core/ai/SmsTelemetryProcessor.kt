package com.ksetrasevakah.core.ai

import android.util.Log
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import com.ksetrasevakah.core.database.dao.TelemetryDao
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.sms.RegexSmsParser
import com.ksetrasevakah.core.vectorstore.RagPipeline
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsTelemetryProcessor @Inject constructor(
    private val telemetryDao: TelemetryDao,
    private val ragPipeline: RagPipeline,
    private val appPreferences: AppPreferencesRepository
) {

    suspend fun process(smsBody: String, timestamp: Long) {
        val t0 = System.currentTimeMillis()
        var extractMs = 0L
        var ragMs = 0L
        try {
            val tExtractStart = System.currentTimeMillis()
            val parsed = RegexSmsParser.parse(smsBody)
            val entity = TelemetryEntity(
                rawSms         = smsBody,
                timestamp      = timestamp,
                motorOn        = parsed.motorOn,
                phaseR         = parsed.phaseR,
                phaseY         = parsed.phaseY,
                phaseB         = parsed.phaseB,
                voltage        = parsed.voltage,
                temperature    = parsed.temperature,
                runtimeMinutes = parsed.runtimeMinutes
            )
            telemetryDao.insert(entity)
            appPreferences.setLastTelemetryIngestAt(System.currentTimeMillis())
            extractMs = System.currentTimeMillis() - tExtractStart

            val tRagStart = System.currentTimeMillis()
            ingestToRag(smsBody)
            ragMs = System.currentTimeMillis() - tRagStart
        } catch (e: Exception) {
            Log.e(TAG, "Ingestion failed", e)
            val tExtractStart = System.currentTimeMillis()
            saveFallbackEntity(smsBody, timestamp)
            appPreferences.setLastTelemetryIngestAt(System.currentTimeMillis())
            extractMs = System.currentTimeMillis() - tExtractStart

            val tRagStart = System.currentTimeMillis()
            ingestToRag(smsBody)
            ragMs = System.currentTimeMillis() - tRagStart
        }
        val totalMs = System.currentTimeMillis() - t0
        appPreferences.setLastIngestTiming(
            totalMs   = totalMs,
            extractMs = extractMs,
            ragMs     = ragMs
        )
        Log.d(
            TAG,
            "Ingest: total=${totalMs}ms, extract+insert=${extractMs}ms, RAG=${ragMs}ms (regex path)"
        )
    }

    private suspend fun ingestToRag(smsBody: String) {
        when (val r = ragPipeline.ingest(smsBody, mapOf("module" to Constants.RAG_MODULE_PUMPIQ))) {
            is Result.Error -> Log.w(TAG, "RAG ingest failed: ${r.message}")
            else -> Unit
        }
    }

    private suspend fun saveFallbackEntity(smsBody: String, timestamp: Long) {
        val entity = TelemetryEntity(
            rawSms    = smsBody,
            timestamp = timestamp,
            motorOn   = smsBody.uppercase().contains("MOTOR ON")
        )
        telemetryDao.insert(entity)
    }

    companion object {
        private const val TAG = "SmsTelemetryProcessor"
    }
}
