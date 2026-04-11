package com.ksetrasevakah.core.ai

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IngestionService : Service() {

    @Inject lateinit var smsTelemetryProcessor: SmsTelemetryProcessor

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
            smsTelemetryProcessor.process(smsBody, smsTimestamp)
            stopSelf(startId)
        }

        return START_NOT_STICKY
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
