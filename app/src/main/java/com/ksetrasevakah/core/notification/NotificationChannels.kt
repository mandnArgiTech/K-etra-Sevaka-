package com.ksetrasevakah.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {

    const val CHANNEL_MOTOR_ALERTS = "motor_alerts"
    const val CHANNEL_PREDICTIONS = "predictions"
    const val CHANNEL_BACKUP = "backup"
    const val CHANNEL_WATCHDOG = "watchdog"
    const val CHANNEL_SECURITY = "security_alerts"
    const val CHANNEL_CRITICAL = "critical_alarm"

    fun createAll(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channels = listOf(
            NotificationChannel(
                CHANNEL_MOTOR_ALERTS,
                "Motor Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical motor state alerts and fault notifications"
            },
            NotificationChannel(
                CHANNEL_PREDICTIONS,
                "AI Predictions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "PumpIQ prediction and analysis notifications"
            },
            NotificationChannel(
                CHANNEL_BACKUP,
                "Backup Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Backup and restore operation notifications"
            },
            NotificationChannel(
                CHANNEL_WATCHDOG,
                "Watchdog Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Forgot-off detection and runtime limit alerts"
            },
            NotificationChannel(
                CHANNEL_SECURITY,
                "Security Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Suraksha security event notifications"
            },
            NotificationChannel(
                CHANNEL_CRITICAL,
                "Critical Alarm",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical security alarm with full-screen alert"
            }
        )

        notificationManager.createNotificationChannels(channels)
    }
}
