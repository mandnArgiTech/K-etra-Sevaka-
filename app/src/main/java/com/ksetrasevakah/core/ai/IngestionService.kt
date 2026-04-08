package com.ksetrasevakah.core.ai

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.ksetrasevakah.core.ai.parser.TelemetryParser
import com.ksetrasevakah.core.ai.prompt.IngestionPrompt
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.TelemetryDao
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IngestionService : Service() {

    @Inject lateinit var engine: MlcLlmEngine
    @Inject lateinit var telemetryDao: TelemetryDao

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val smsBody = intent?.getStringExtra(EXTRA_SMS_BODY)
        val smsTimestamp = intent?.getLongExtra(EXTRA_SMS_TIMESTAMP, System.currentTimeMillis())
            ?: System.currentTimeMillis()

        if (smsBody.isNullOrBlank()) {
            Log.w(TAG, "No SMS body in intent, stopping")
            stopSelf(startId)
            return START_NOT_STICKY
        }

        serviceScope.launch {
            processSmsTelemetry(smsBody, smsTimestamp)
            stopSelf(startId)
        }

        return START_NOT_STICKY
    }

    private suspend fun processSmsTelemetry(smsBody: String, timestamp: Long) {
        try {
            val prompt = IngestionPrompt.getExtractionPrompt(smsBody)
            val fullResponse = engine.generate(prompt, Constants.INGESTION_MODEL_ID)
                .fold(StringBuilder()) { acc, token -> acc.append(token) }
                .toString()

            when (val parsed = TelemetryParser.parseResponse(fullResponse)) {
                is Result.Success -> {
                    val entity = TelemetryEntity(
                        rawSms = smsBody,
                        timestamp = timestamp,
                        motorOn = parsed.data.motorOn,
                        phaseR = parsed.data.phaseR,
                        phaseY = parsed.data.phaseY,
                        phaseB = parsed.data.phaseB,
                        voltage = parsed.data.voltage,
                        temperature = parsed.data.temperature,
                        runtimeMinutes = parsed.data.runtimeMinutes
                    )
                    telemetryDao.insert(entity)
                    Log.d(TAG, "Telemetry saved for SMS at $timestamp")
                }
                is Result.Error -> {
                    Log.e(TAG, "Failed to parse model output: ${parsed.message}")
                    saveFallbackEntity(smsBody, timestamp)
                }
                is Result.Loading -> Unit
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ingestion failed", e)
            saveFallbackEntity(smsBody, timestamp)
        }
    }

    private suspend fun saveFallbackEntity(smsBody: String, timestamp: Long) {
        val entity = TelemetryEntity(
            rawSms = smsBody,
            timestamp = timestamp,
            motorOn = smsBody.uppercase().contains("MOTOR ON")
        )
        telemetryDao.insert(entity)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Telemetry Ingestion",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification =
        Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Processing SMS")
            .setContentText("Extracting telemetry data...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        private const val TAG = "IngestionService"
        private const val CHANNEL_ID = "ingestion_channel"
        private const val NOTIFICATION_ID = 1001
        const val EXTRA_SMS_BODY = "extra_sms_body"
        const val EXTRA_SMS_TIMESTAMP = "extra_sms_timestamp"
    }
}
