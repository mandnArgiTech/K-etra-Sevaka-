package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.notification.CriticalAlarmManager
import com.ksetrasevakah.core.notification.KsetraNotificationManager
import com.ksetrasevakah.core.notification.NotificationDismisser
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
    private lateinit var dismisser: NotificationDismisser
    private lateinit var router: ThreatRouter

    private val testEvent = TapoEvent(
        cameraName = "Front Door",
        eventType = "PERSON",
        timestamp = System.currentTimeMillis(),
        rawTitle = "Front Door: Person",
        rawText = "Person detected",
        sbnKey = "test|0|pkg"
    )

    private val activeCamera = CameraConfig(
        id = 1L,
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
        dismisser = mockk(relaxed = true)

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
    fun `route DROP mode dismisses and skips classify`() = runTest {
        val dropCamera = activeCamera.copy(mode = CameraMode.DROP)
        coEvery { cameraConfigRepository.getByName("Front Door") } returns Result.Success(dropCamera)

        router.route(testEvent, dismisser)

        verify { dismisser.dismiss(testEvent.sbnKey) }
        coVerify(exactly = 0) { classifier.classify(any()) }
    }

    @Test
    fun `route ACTIVE LOW dismisses Tapo notification`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.LogOnly(
            threatLevel = ThreatLevel.LOW,
            confidence = 0.5f,
            summary = "Routine"
        )

        router.route(testEvent, dismisser)

        coVerify { eventRepository.insert(any()) }
        verify { dismisser.dismiss(testEvent.sbnKey) }
    }

    @Test
    fun `route ACTIVE MEDIUM dismisses Tapo notification`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.LogOnly(
            threatLevel = ThreatLevel.MEDIUM,
            confidence = 0.6f,
            summary = "Caution"
        )

        router.route(testEvent, dismisser)

        verify { dismisser.dismiss(testEvent.sbnKey) }
    }

    @Test
    fun `route ACTIVE HIGH does not dismiss Tapo notification`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.Notify(
            threatLevel = ThreatLevel.HIGH,
            confidence = 0.8f,
            summary = "Suspicious activity",
            title = "HIGH: Person detected"
        )

        router.route(testEvent, dismisser)

        verify(exactly = 0) { dismisser.dismiss(any()) }
        verify { notificationManager.postSecurityAlert(any(), any()) }
    }

    @Test
    fun `route ACTIVE CRITICAL does not dismiss Tapo notification`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.CriticalAlarm(
            threatLevel = ThreatLevel.CRITICAL,
            confidence = 0.95f,
            summary = "Camera tampering",
            title = "CRITICAL: Tampering",
            cameraName = "Front Door"
        )

        router.route(testEvent, dismisser)

        verify(exactly = 0) { dismisser.dismiss(any()) }
        verify { criticalAlarmManager.triggerCriticalAlarm(any()) }
    }

    @Test
    fun `route SILENT dismisses for HIGH threat`() = runTest {
        val silentCamera = activeCamera.copy(mode = CameraMode.SILENT)
        coEvery { cameraConfigRepository.getByName("Front Door") } returns Result.Success(silentCamera)
        coEvery { classifier.classify(any()) } returns ThreatAction.Notify(
            threatLevel = ThreatLevel.HIGH,
            confidence = 0.8f,
            summary = "Suspicious",
            title = "Alert"
        )

        router.route(testEvent, dismisser)

        coVerify { eventRepository.insert(any()) }
        verify { dismisser.dismiss(testEvent.sbnKey) }
        verify(exactly = 0) { notificationManager.postSecurityAlert(any(), any()) }
    }

    @Test
    fun `route Drop action dismisses without persisting`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.Drop(reason = "Duplicate")

        router.route(testEvent, dismisser)

        verify { dismisser.dismiss(testEvent.sbnKey) }
        coVerify(exactly = 0) { eventRepository.insert(any()) }
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

        val newEvent = testEvent.copy(cameraName = "New Camera", sbnKey = "k2")
        router.route(newEvent, dismisser)

        coVerify { cameraConfigRepository.insert(any()) }
    }

    @Test
    fun `route uses fallback when classify throws`() = runTest {
        coEvery { classifier.classify(any()) } throws RuntimeException("mlc")

        router.route(testEvent, dismisser)

        coVerify { eventRepository.insert(any()) }
        verify { dismisser.dismiss(testEvent.sbnKey) }
    }

    @Test
    fun `route still dismisses Tapo when persist fails for LOW`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.LogOnly(
            threatLevel = ThreatLevel.LOW,
            confidence = 0.5f,
            summary = "Routine"
        )
        coEvery { eventRepository.insert(any()) } returns Result.Error("db")

        router.route(testEvent, dismisser)

        verify { dismisser.dismiss(testEvent.sbnKey) }
    }

    @Test
    fun `route updates lastSeen for known camera`() = runTest {
        coEvery { classifier.classify(any()) } returns ThreatAction.LogOnly(
            threatLevel = ThreatLevel.LOW,
            confidence = 0.5f,
            summary = "Activity"
        )

        router.route(testEvent, dismisser)

        coVerify { cameraConfigRepository.updateLastSeen("Front Door", testEvent.timestamp) }
    }
}
