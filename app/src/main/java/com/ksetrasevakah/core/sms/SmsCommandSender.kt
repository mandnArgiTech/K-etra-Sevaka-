package com.ksetrasevakah.core.sms

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.sms.model.SmsCommand
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface SmsCommandSender {
    fun sendCommand(command: SmsCommand): Result<Unit>
}

class DefaultSmsCommandSender @Inject constructor(
    @ApplicationContext private val context: Context,
    private val smsManager: SmsManager
) : SmsCommandSender {

    override fun sendCommand(command: SmsCommand): Result<Unit> {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure(SecurityException("SEND_SMS permission not granted"))
        }

        return try {
            val sentIntent = PendingIntent.getBroadcast(
                context,
                0,
                Intent(ACTION_SMS_SENT),
                PendingIntent.FLAG_IMMUTABLE
            )
            smsManager.sendTextMessage(
                Constants.TARO_PANEL_NUMBER,
                null,
                command.text,
                sentIntent,
                null
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val ACTION_SMS_SENT = "com.ksetrasevakah.SMS_SENT"
    }
}
