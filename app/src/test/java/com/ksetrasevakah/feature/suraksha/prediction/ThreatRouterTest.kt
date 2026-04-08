package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.notification.CriticalAlarmManager
import com.ksetrasevakah.core.notification.KsetraNotificationManager
import com.ksetrasevakah.core.notification.model.TapoEvent
import com.ksetrasevakah.feature.suraksha.domain.model.CameraConfig
import com.ksetrasevakah.feature.suraksha.domain.model.CameraMode
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import com.ksetrasevakah.feature.suraksha.prediction.model.ThreatAction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ThreatRouterTest {

    private lateinit var classifier: ThreatClassifier
    private lateinit var eventRepository: SecurityEventRepository
    private lateinit var cameraConfigRepository: CameraConfigRepository
    private lateinit var notificationManager: KsetraNotificationManager
    private lateinit var criticalAlarmManager: CriticalAlarmManager
    private lateinit var router: ThreatRouter

    private val testEvent = TapoEvent(
        cameraName = "Front Door",
        eventType = "PERSON",
        timestamp = System.currentTimeMillis(),
        rawTitle = "Front Door: Person",
        rawText = "Person detected"
    )

    private val activeCamera = CameraConfig(
        id = "Front Door",
        cameraName = "Front Door",
        mode = CameraMode.ACTIVE,
        lastSeen = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis() - 86400000
    )

    @BeforeEach
    fun setup() {
        classifier = mockk(relaxed = true)
        eventRepository = mockk(relaxed = true)
        cameraConfigRepository = mockk(relaxed = true)
        notificationManager = mockk(relaxed = true)
        criticalAlarmManager = mockk(relaxed = true)

        router = ThreatRouter(
            classifier = classifier,
            eventRepository = eventRepository,
            cameraConfigRepository = cameraConfigRepository,
            notificationManager = notificationManager,
            criticalAlarmManager = criticalAlarmManager
        )

        coEvery { cameraConfigRepository.getByName(any()) } returns Result.Success(activeCamera)
        coEvery { cameraConfigRepository.updateLastSeen(any(), any()) } returns Result.Success(Unit)
        coEvery { eventRepository.insert(any()) } returns Result.Success(1L)
    }

    @Test
    fun `route skips processing when camera mode is DROP`() = runTest {
        val dropCamera = activeCamera.copy(mode = CameraMode.DROP)
        coEvery { cameraConfigRepository.getByName("Front Door") } returns Result.Success(dropCamera)

        router.route(testEvent)

        coVerify(exactly = 0) { classifier.classify(any()) }
    }

    @Test
    fun `route persists event for ACTIVE camera`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.LogOnly(
            threatLevel = ThreatLevel.LOW,
            confidence = 0.5f,
            summary = "Routine"
        )

        router.route(testEvent)

        coVerify { eventRepository.insert(any()) }
    }

    @Test
    fun `route sends notification for Notify action on ACTIVE camera`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.Notify(
            threatLevel = ThreatLevel.HIGH,
            confidence = 0.8f,
            summary = "Suspicious activity",
            title = "HIGH: Person detected"
        )

        router.route(testEvent)

        verify { notificationManager.postSecurityAlert(any(), any()) }
    }

    @Test
    fun `route triggers critical alarm for CriticalAlarm action`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.CriticalAlarm(
            threatLevel = ThreatLevel.CRITICAL,
            confidence = 0.95f,
            summary = "Camera tampering",
            title = "CRITICAL: Tampering",
            cameraName = "Front Door"
        )

        router.route(testEvent)

        verify { criticalAlarmManager.triggerCriticalAlarm(any()) }
    }

    @Test
    fun `route does not alert for SILENT camera`() = runTest {
        val silentCamera = activeCamera.copy(mode = CameraMode.SILENT)
        coEvery { cameraConfigRepository.getByName("Front Door") } returns Result.Success(silentCamera)
        coEvery { classifier.classify(any()) } returns ThreatAction.Notify(
            threatLevel = ThreatLevel.HIGH,
            confidence = 0.8f,
            summary = "Suspicious",
            title = "Alert"
        )

        router.route(testEvent)

        coVerify { eventRepository.insert(any()) }
        verify(exactly = 0) { notificationManager.postSecurityAlert(any(), any()) }
    }

    @Test
    fun `route auto-registers unknown camera`() = runTest {
        coEvery { cameraConfigRepository.getByName("New Camera") } returns Result.Success(null)
        coEvery { cameraConfigRepository.insert(any()) } returns Result.Success(1L)
        coEvery { classifier.classify(any()) } returns ThreatAction.LogOnly(
            threatLevel = ThreatLevel.LOW,
            confidence = 0.5f,
            summary = "Activity"
        )

        val newEvent = testEvent.copy(cameraName = "New Camera")
        router.route(newEvent)

        coVerify { cameraConfigRepository.insert(any()) }
    }

    @Test
    fun `route updates lastSeen for known camera`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.LogOnly(
            threatLevel = ThreatLevel.LOW,
            confidence = 0.5f,
            summary = "Activity"
        )

        router.route(testEvent)

        coVerify { cameraConfigRepository.updateLastSeen("Front Door", testEvent.timestamp) }
    }

    @Test
    fun `route skips Drop action without persisting`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.Drop(reason = "Duplicate")

        router.route(testEvent)

        coVerify(exactly = 0) { eventRepository.insert(any()) }
    }
}
