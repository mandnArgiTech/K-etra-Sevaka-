package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.notification.CriticalAlarmManager
import com.ksetrasevakah.core.notification.KsetraNotificationManager
import com.ksetrasevakah.core.notification.NotificationDismisser
import com.ksetrasevakah.core.notification.model.CriticalOverlayData
import com.ksetrasevakah.core.notification.model.TapoEvent
import com.ksetrasevakah.feature.suraksha.domain.model.CameraConfig
import com.ksetrasevakah.feature.suraksha.domain.model.CameraMode
import com.ksetrasevakah.feature.suraksha.domain.model.EventType
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import com.ksetrasevakah.feature.suraksha.prediction.model.ThreatAction
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreatRouter @Inject constructor(
    private val classifier: ThreatClassifier,
    private val eventRepository: SecurityEventRepository,
    private val cameraConfigRepository: CameraConfigRepository,
    private val notificationManager: KsetraNotificationManager,
    private val criticalAlarmManager: CriticalAlarmManager
) {

    suspend fun route(event: TapoEvent, dismisser: NotificationDismisser) {
        val cameraConfig = cameraConfigRepository.getByName(event.cameraName).getOrNull()
        val mode = cameraConfig?.mode ?: CameraMode.ACTIVE

        if (!mode.shouldProcess) {
            dismisser.dismiss(event.sbnKey)
            return
        }

        when (cameraConfigRepository.updateLastSeen(event.cameraName, event.timestamp)) {
            is Result.Error -> { /* non-fatal: continue routing */ }
            else -> Unit
        }

        if (cameraConfig == null) {
            when (
                cameraConfigRepository.insert(
                    CameraConfig(
                        cameraName = event.cameraName,
                        mode = CameraMode.ACTIVE,
                        lastSeen = event.timestamp,
                        createdAt = event.timestamp
                    )
                )
            ) {
                is Result.Error -> { /* non-fatal: one-off event still classified */ }
                else -> Unit
            }
        }

        val action = try {
            classifier.classify(event)
        } catch (_: Exception) {
            ThreatAction.LogOnly(
                threatLevel = ThreatLevel.MEDIUM,
                confidence = 0f,
                summary = "Classification unavailable; event noted for review."
            )
        }

        if (action is ThreatAction.Drop) {
            dismisser.dismiss(event.sbnKey)
            return
        }

        try {
            persistEvent(event, action)
        } catch (_: Exception) {
            // Still apply dismissal rules so Tapo anti-fatigue works if DB write fails
        }

        val threatLevel = extractActionDetails(action).first
        if (!mode.shouldAlert) {
            dismisser.dismiss(event.sbnKey)
        } else if (!threatLevel.isAlertable) {
            dismisser.dismiss(event.sbnKey)
        }

        if (!mode.shouldAlert) return

        try {
            executeAction(action)
        } catch (_: Exception) {
            // HIGH/CRITICAL Tapo notification remains if app alert path fails
        }
    }

    private suspend fun persistEvent(event: TapoEvent, action: ThreatAction) {
        val (threatLevel, confidence, summary) = extractActionDetails(action)
        val calendar = Calendar.getInstance().apply { timeInMillis = event.timestamp }

        val securityEvent = SecurityEvent(
            cameraName = event.cameraName,
            eventType = EventType.fromString(event.eventType),
            threatLevel = threatLevel,
            confidence = confidence,
            originTimestamp = event.timestamp,
            receivedTimestamp = System.currentTimeMillis(),
            hourOfDay = calendar.get(Calendar.HOUR_OF_DAY),
            summary = summary
        )

        when (val r = eventRepository.insert(securityEvent)) {
            is Result.Success -> Unit
            is Result.Error -> throw IllegalStateException(r.message ?: "Security event insert failed")
            is Result.Loading -> Unit
        }
    }

    private fun executeAction(action: ThreatAction) {
        when (action) {
            is ThreatAction.LogOnly -> { /* already persisted */ }
            is ThreatAction.Notify -> {
                notificationManager.postSecurityAlert(action.title, action.summary)
            }
            is ThreatAction.CriticalAlarm -> {
                val overlayData = CriticalOverlayData(
                    title = action.title,
                    message = action.summary,
                    cameraName = action.cameraName,
                    threatLevel = action.threatLevel
                )
                criticalAlarmManager.triggerCriticalAlarm(overlayData)
            }
            is ThreatAction.Drop -> { /* no-op */ }
        }
    }

    private fun extractActionDetails(action: ThreatAction): Triple<ThreatLevel, Float, String> =
        when (action) {
            is ThreatAction.LogOnly -> Triple(action.threatLevel, action.confidence, action.summary)
            is ThreatAction.Notify -> Triple(action.threatLevel, action.confidence, action.summary)
            is ThreatAction.CriticalAlarm -> Triple(action.threatLevel, action.confidence, action.summary)
            is ThreatAction.Drop -> Triple(ThreatLevel.LOW, 0f, action.reason)
        }
}
