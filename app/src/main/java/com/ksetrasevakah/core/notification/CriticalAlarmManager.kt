package com.ksetrasevakah.core.notification

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.notification.model.CriticalOverlayData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CriticalAlarmManager @Inject constructor(
    private val context: Context,
    private val notificationManager: KsetraNotificationManager
) {

    private var alarmJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun triggerCriticalAlarm(data: CriticalOverlayData) {
        postCriticalNotification(data)
        startAlarmSequence()
    }

    fun dismiss() {
        alarmJob?.cancel()
        alarmJob = null
        notificationManager.cancel(KsetraNotificationManager.CRITICAL_ALARM_ID)
    }

    internal fun postCriticalNotification(data: CriticalOverlayData) {
        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_CRITICAL)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(data.title)
            .setContentText(data.message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.notify(KsetraNotificationManager.CRITICAL_ALARM_ID, notification)
    }

    internal fun startAlarmSequence() {
        alarmJob?.cancel()
        alarmJob = scope.launch {
            vibrate()
            playAlarmTone()
            delay(Constants.CRITICAL_ALARM_DURATION_MS)
            dismiss()
        }
    }

    internal fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 500, 200, 500, 200, 500)
        val effect = VibrationEffect.createWaveform(pattern, -1)
        vibrator.vibrate(
            effect,
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        )
    }

    internal fun playAlarmTone() {
        try {
            val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 3000)
        } catch (_: Exception) {
            // Tone generation not available on all devices
        }
    }
}
