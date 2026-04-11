package com.ksetrasevakah.core.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.core.content.ContextCompat
import com.ksetrasevakah.core.ai.IngestionService
import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SmsReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SmsReceiverEntryPoint {
        fun appPreferencesRepository(): AppPreferencesRepository
    }

    var onSmsReceived: ((sender: String, body: String) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val appPreferences = EntryPointAccessors.fromApplication(
            context.applicationContext,
            SmsReceiverEntryPoint::class.java
        ).appPreferencesRepository()

        val normalizedPanel = runBlocking { appPreferences.panelNumber.first() }
            .replace("\\s".toRegex(), "")

        val messages = SmsParser.extractFromIntent(intent)
        for (sms in messages) {
            val normalizedSender = sms.sender.replace("\\s".toRegex(), "")

            if (normalizedSender.endsWith(normalizedPanel) ||
                normalizedPanel.endsWith(normalizedSender)
            ) {
                Log.d(TAG, "SMS from panel: ${sms.body}")
                onSmsReceived?.invoke(sms.sender, sms.body)
                val ingest = Intent(context, IngestionService::class.java).apply {
                    putExtra(IngestionService.EXTRA_SMS_BODY, sms.body)
                    putExtra(IngestionService.EXTRA_SMS_TIMESTAMP, System.currentTimeMillis())
                }
                try {
                    ContextCompat.startForegroundService(context, ingest)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start IngestionService", e)
                }
            }
        }
    }

    companion object {
        private const val TAG = "SmsReceiver"
    }
}
