package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.notification.CriticalAlarmManager
import com.ksetrasevakah.core.notification.KsetraNotificationManager
import com.ksetrasevakah.core.notification.model.CriticalOverlayData
import com.ksetrasevakah.core.notification.model.TapoEvent
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

    suspend fun route(event: TapoEvent) {
        val cameraConfig = cameraConfigRepository.getByName(event.cameraName).getOrNull()
        val mode = cameraConfig?.mode ?: CameraMode.ACTIVE

        if (!mode.shouldProcess) return

        cameraConfigRepository.updateLastSeen(event.cameraName, event.timestamp)

        if (cameraConfig == null) {
            autoRegisterCamera(event.cameraName, event.timestamp)
        }

        val action = classifier.classify(event)

        if (action is ThreatAction.Drop) return

        persistEvent(event, action)

        if (!mode.shouldAlert) return

        executeAction(action)
    }

    private suspend fun autoRegisterCamera(cameraName: String, timestamp: Long) {
        val config = com.ksetrasevakah.feature.suraksha.domain.model.CameraConfig(
            cameraName = cameraName,
            mode = CameraMode.ACTIVE,
            lastSeen = timestamp,
            createdAt = timestamp
        )
        cameraConfigRepository.insert(config)
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

        eventRepository.insert(securityEvent)
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
