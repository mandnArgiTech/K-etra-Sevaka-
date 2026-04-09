package com.ksetrasevakah.core.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.notification.model.TapoEvent
import com.ksetrasevakah.feature.suraksha.prediction.ThreatRouter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TapoNotificationListener : NotificationListenerService(), NotificationDismisser {

    @Inject lateinit var threatRouter: ThreatRouter

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun dismiss(sbnKey: String) {
        if (sbnKey.isBlank()) return
        try {
            cancelNotification(sbnKey)
        } catch (e: Exception) {
            Log.w(TAG, "cancelNotification failed for key=$sbnKey", e)
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        if (sbn.packageName != Constants.TAPO_PACKAGE_NAME) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return
        val title = extras.getCharSequence("android.title")?.toString() ?: return
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val whenMs = notification.`when`

        val now = System.currentTimeMillis()
        val originTimestamp = when {
            whenMs <= 0L -> now
            whenMs > now + FUTURE_SKEW_MS -> now
            now - whenMs > Constants.NOTIFICATION_WHEN_MAX_AGE_MS -> now
            else -> whenMs
        }

        val event: TapoEvent = TapoNotificationParser.parse(
            title = title,
            text = text,
            originTimestamp = originTimestamp,
            sbnKey = sbn.key
        )

        scope.launch {
            threatRouter.route(event, this@TapoNotificationListener)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private companion object {
        const val TAG = "TapoNotificationListener"
        const val FUTURE_SKEW_MS = 60_000L
    }
}
