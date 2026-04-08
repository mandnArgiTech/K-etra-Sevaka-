package com.ksetrasevakah.core.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.ksetrasevakah.core.common.Constants

class SmsReceiver : BroadcastReceiver() {

    var onSmsReceived: ((sender: String, body: String) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = SmsParser.extractFromIntent(intent)
        for (sms in messages) {
            val normalizedSender = sms.sender.replace("\\s".toRegex(), "")
            val normalizedPanel = Constants.TARO_PANEL_NUMBER.replace("\\s".toRegex(), "")

            if (normalizedSender.endsWith(normalizedPanel) ||
                normalizedPanel.endsWith(normalizedSender)
            ) {
                Log.d(TAG, "SMS from panel: ${sms.body}")
                onSmsReceived?.invoke(sms.sender, sms.body)
            }
        }
    }

    companion object {
        private const val TAG = "SmsReceiver"
    }
}
