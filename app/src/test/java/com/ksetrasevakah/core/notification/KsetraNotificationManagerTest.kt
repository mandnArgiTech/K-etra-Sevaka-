package com.ksetrasevakah.core.notification

import android.app.NotificationManager
import android.content.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KsetraNotificationManagerTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager
    private lateinit var ksetraNotificationManager: KsetraNotificationManager

    @BeforeEach
    fun setup() {
        context = mockk(relaxed = true)
        notificationManager = mockk(relaxed = true)
        every { context.getSystemService(Context.NOTIFICATION_SERVICE) } returns notificationManager
        ksetraNotificationManager = KsetraNotificationManager(context)
    }

    @Test
    fun `cancel removes specific notification`() {
        ksetraNotificationManager.cancel(KsetraNotificationManager.MOTOR_ALERT_ID)

        verify { notificationManager.cancel(KsetraNotificationManager.MOTOR_ALERT_ID) }
    }

    @Test
    fun `cancelAll removes all notifications`() {
        ksetraNotificationManager.cancelAll()

        verify { notificationManager.cancelAll() }
    }

    @Test
    fun `motor alert notification ID is 1001`() {
        assertEquals(1001, KsetraNotificationManager.MOTOR_ALERT_ID)
    }

    @Test
    fun `notification IDs are distinct`() {
        val ids = setOf(
            KsetraNotificationManager.MOTOR_ALERT_ID,
            KsetraNotificationManager.PREDICTION_ID,
            KsetraNotificationManager.BACKUP_ID,
            KsetraNotificationManager.WATCHDOG_ID
        )
        assertEquals(4, ids.size)
    }
}
