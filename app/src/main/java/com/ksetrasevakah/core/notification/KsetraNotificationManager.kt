package com.ksetrasevakah.core.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import javax.inject.Inject

class KsetraNotificationManager @Inject constructor(
    private val context: Context
) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun postMotorAlert(title: String, message: String, notificationId: Int = MOTOR_ALERT_ID) {
        post(
            channelId = NotificationChannels.CHANNEL_MOTOR_ALERTS,
            title = title,
            message = message,
            notificationId = notificationId,
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    fun postPrediction(title: String, message: String, notificationId: Int = PREDICTION_ID) {
        post(
            channelId = NotificationChannels.CHANNEL_PREDICTIONS,
            title = title,
            message = message,
            notificationId = notificationId,
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }

    fun postBackupStatus(title: String, message: String, notificationId: Int = BACKUP_ID) {
        post(
            channelId = NotificationChannels.CHANNEL_BACKUP,
            title = title,
            message = message,
            notificationId = notificationId,
            priority = NotificationCompat.PRIORITY_LOW
        )
    }

    fun postSecurityAlert(title: String, message: String, notificationId: Int = SECURITY_ALERT_ID) {
        post(
            channelId = NotificationChannels.CHANNEL_SECURITY,
            title = title,
            message = message,
            notificationId = notificationId,
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    fun postWatchdogAlert(title: String, message: String, notificationId: Int = WATCHDOG_ID) {
        post(
            channelId = NotificationChannels.CHANNEL_WATCHDOG,
            title = title,
            message = message,
            notificationId = notificationId,
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    fun cancel(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    fun cancelAll() {
        notificationManager.cancelAll()
    }

    private fun post(
        channelId: String,
        title: String,
        message: String,
        notificationId: Int,
        priority: Int
    ) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = launchIntent?.let {
            PendingIntent.getActivity(
                context, 0, it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(priority)
            .setAutoCancel(true)
            .apply { pendingIntent?.let { setContentIntent(it) } }
            .build()

        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val MOTOR_ALERT_ID = 1001
        const val PREDICTION_ID = 1002
        const val BACKUP_ID = 1003
        const val WATCHDOG_ID = 1004
        const val SECURITY_ALERT_ID = 1005
        const val CRITICAL_ALARM_ID = 1006
    }
}
