package com.ksetrasevakah.core.notification

import android.app.NotificationManager
import android.content.Context
import com.ksetrasevakah.core.notification.model.CriticalOverlayData
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CriticalAlarmManagerTest {

    private lateinit var context: Context
    private lateinit var notificationManager: KsetraNotificationManager
    private lateinit var alarmManager: CriticalAlarmManager

    private val testData = CriticalOverlayData(
        title = "CRITICAL: Tampering",
        message = "Camera tampering on Front Door",
        cameraName = "Front Door",
        threatLevel = ThreatLevel.CRITICAL
    )

    @BeforeEach
    fun setup() {
        context = mockk(relaxed = true)
        notificationManager = mockk(relaxed = true)

        val androidNotificationManager = mockk<NotificationManager>(relaxed = true)
        every { context.getSystemService(Context.NOTIFICATION_SERVICE) } returns androidNotificationManager

        alarmManager = spyk(CriticalAlarmManager(context, notificationManager)) {
            every { postCriticalNotification(any()) } returns Unit
            every { startAlarmSequence() } returns Unit
            every { vibrate() } returns Unit
            every { playAlarmTone() } returns Unit
        }
    }

    @Test
    fun `triggerCriticalAlarm posts notification`() {
        alarmManager.triggerCriticalAlarm(testData)

        verify { alarmManager.postCriticalNotification(testData) }
    }

    @Test
    fun `triggerCriticalAlarm starts alarm sequence`() {
        alarmManager.triggerCriticalAlarm(testData)

        verify { alarmManager.startAlarmSequence() }
    }

    @Test
    fun `dismiss cancels alarm notification`() {
        alarmManager.dismiss()

        verify { notificationManager.cancel(KsetraNotificationManager.CRITICAL_ALARM_ID) }
    }

    @Test
    fun `triggerCriticalAlarm calls both post and alarm`() {
        alarmManager.triggerCriticalAlarm(testData)

        verify { alarmManager.postCriticalNotification(testData) }
        verify { alarmManager.startAlarmSequence() }
    }

    @Test
    fun `dismiss can be called multiple times safely`() {
        alarmManager.dismiss()
        alarmManager.dismiss()

        verify(exactly = 2) { notificationManager.cancel(KsetraNotificationManager.CRITICAL_ALARM_ID) }
    }
}
