package com.ksetrasevakah.core.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
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
class TapoNotificationListener : NotificationListenerService() {

    @Inject lateinit var threatRouter: ThreatRouter

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        if (sbn.packageName != Constants.TAPO_PACKAGE_NAME) return

        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return
        val title = extras.getCharSequence("android.title")?.toString() ?: return
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val whenMs = notification.`when`

        if (whenMs > 0 && System.currentTimeMillis() - whenMs > Constants.NOTIFICATION_WHEN_MAX_AGE_MS) {
            return
        }

        val event: TapoEvent = TapoNotificationParser.parse(title, text, whenMs)

        scope.launch {
            threatRouter.route(event)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
