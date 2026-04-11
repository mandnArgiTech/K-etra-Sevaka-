package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.ai.MlcLlmEngine
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.notification.model.TapoEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import com.ksetrasevakah.feature.suraksha.prediction.model.ThreatAction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.TimeZone

class ThreatClassifierTest {

    private lateinit var engine: MlcLlmEngine
    private lateinit var spikeDetector: ActivitySpikeDetector
    private lateinit var classifier: ThreatClassifier

    private val tamperEvent = TapoEvent(
        cameraName = "Backyard",
        eventType = "TAMPERING",
        timestamp = atUtcHour(12),
        rawTitle = "Backyard: Tampering",
        rawText = "Camera tampering detected"
    )

    @BeforeEach
    fun setup() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        engine = mockk(relaxed = true)
        spikeDetector = mockk(relaxed = true)
        classifier = ThreatClassifier(engine, spikeDetector)
        coEvery { spikeDetector.isActivitySpike(any(), any()) } returns false
        coEvery { spikeDetector.isCoordinatedActivity(any()) } returns false
    }

    private fun personAt(hour: Int): TapoEvent = TapoEvent(
        cameraName = "Front Door",
        eventType = "PERSON",
        timestamp = atUtcHour(hour),
        rawTitle = "Front Door: Person detected",
        rawText = "A person was detected"
    )

    @Test
    fun `applyRules returns CriticalAlarm for TAMPERING`() = runTest {
        val result = classifier.applyRules(tamperEvent)

        assertTrue(result is ThreatAction.CriticalAlarm)
        assertEquals(ThreatLevel.CRITICAL, (result as ThreatAction.CriticalAlarm).threatLevel)
    }

    @Test
    fun `applyRules PERSON hour 10 LogOnly LOW`() = runTest {
        val result = classifier.applyRules(personAt(10))
        assertTrue(result is ThreatAction.LogOnly)
        assertEquals(ThreatLevel.LOW, (result as ThreatAction.LogOnly).threatLevel)
    }

    @Test
    fun `applyRules PERSON hour 14 LogOnly LOW`() = runTest {
        val result = classifier.applyRules(personAt(14))
        assertTrue(result is ThreatAction.LogOnly)
        assertEquals(ThreatLevel.LOW, (result as ThreatAction.LogOnly).threatLevel)
    }

    @Test
    fun `applyRules PERSON hour 19 LogOnly MEDIUM`() = runTest {
        val result = classifier.applyRules(personAt(19))
        assertTrue(result is ThreatAction.LogOnly)
        assertEquals(ThreatLevel.MEDIUM, (result as ThreatAction.LogOnly).threatLevel)
    }

    @Test
    fun `applyRules PERSON hour 23 Notify HIGH`() = runTest {
        val result = classifier.applyRules(personAt(23))
        assertTrue(result is ThreatAction.Notify)
        assertEquals(ThreatLevel.HIGH, (result as ThreatAction.Notify).threatLevel)
    }

    @Test
    fun `applyRules PERSON hour 2 Notify HIGH`() = runTest {
        val result = classifier.applyRules(personAt(2))
        assertTrue(result is ThreatAction.Notify)
        assertEquals(ThreatLevel.HIGH, (result as ThreatAction.Notify).threatLevel)
    }

    @Test
    fun `applyRules PERSON hour 10 spike elevates to MEDIUM`() = runTest {
        coEvery { spikeDetector.isActivitySpike("Front Door", any()) } returns true
        val result = classifier.applyRules(personAt(10))
        assertTrue(result is ThreatAction.LogOnly)
        assertEquals(ThreatLevel.MEDIUM, (result as ThreatAction.LogOnly).threatLevel)
    }

    @Test
    fun `applyRules PERSON hour 23 spike elevates to CRITICAL CriticalAlarm`() = runTest {
        coEvery { spikeDetector.isActivitySpike("Front Door", any()) } returns true
        val result = classifier.applyRules(personAt(23))
        assertTrue(result is ThreatAction.CriticalAlarm)
        assertEquals(ThreatLevel.CRITICAL, (result as ThreatAction.CriticalAlarm).threatLevel)
    }

    @Test
    fun `applyRules PERSON hour 1 coordinated CRITICAL`() = runTest {
        coEvery { spikeDetector.isCoordinatedActivity(any()) } returns true
        val result = classifier.applyRules(personAt(1))
        assertTrue(result is ThreatAction.CriticalAlarm)
        assertEquals(ThreatLevel.CRITICAL, (result as ThreatAction.CriticalAlarm).threatLevel)
    }

    @Test
    fun `applyRules PERSON hour 15 coordinated ignored daytime window`() = runTest {
        coEvery { spikeDetector.isCoordinatedActivity(any()) } returns true
        val result = classifier.applyRules(personAt(15))
        assertTrue(result is ThreatAction.LogOnly)
        assertEquals(ThreatLevel.LOW, (result as ThreatAction.LogOnly).threatLevel)
    }

    @Test
    fun `applyRules returns null for UNKNOWN`() = runTest {
        val unknown = personAt(10).copy(eventType = "UNKNOWN")
        assertEquals(null, classifier.applyRules(unknown))
    }

    @Test
    fun `classify TAMPERING without AI`() = runTest {
        val result = classifier.classify(tamperEvent)
        assertTrue(result is ThreatAction.CriticalAlarm)
        verify(exactly = 0) { engine.generate(any(), any()) }
    }

    @Test
    fun `classify PERSON without AI`() = runTest {
        val result = classifier.classify(personAt(11))
        assertTrue(result is ThreatAction.LogOnly)
        verify(exactly = 0) { engine.generate(any(), any()) }
    }

    @Test
    fun `classify UNKNOWN uses AI`() = runTest {
        val unknown = personAt(10).copy(eventType = "UNKNOWN")
        every { engine.generate(any(), Constants.ORCHESTRATOR_MODEL_ID) } returns flowOf("LOW\nnoise")
        val result = classifier.classify(unknown)
        assertTrue(result is ThreatAction.LogOnly)
        verify(atLeast = 1) { engine.generate(any(), Constants.ORCHESTRATOR_MODEL_ID) }
    }

    @Test
    fun `parseAiResponse maps CRITICAL to CriticalAlarm`() {
        val result = classifier.parseAiResponse("CRITICAL\nImmediate danger", personAt(12))
        assertTrue(result is ThreatAction.CriticalAlarm)
    }

    @Test
    fun `parseAiResponse maps LOW to LogOnly`() {
        val result = classifier.parseAiResponse("LOW\nRoutine activity", personAt(12))
        assertTrue(result is ThreatAction.LogOnly)
        assertEquals(ThreatLevel.LOW, (result as ThreatAction.LogOnly).threatLevel)
    }

    @Test
    fun `fallbackClassification returns MEDIUM for PERSON`() {
        val result = classifier.fallbackClassification(personAt(12))
        assertTrue(result is ThreatAction.LogOnly)
        assertEquals(ThreatLevel.MEDIUM, (result as ThreatAction.LogOnly).threatLevel)
    }

    @Test
    fun `classify falls back on AI exception`() = runTest {
        val unknown = personAt(10).copy(eventType = "UNKNOWN")
        coEvery { spikeDetector.isActivitySpike(any(), any()) } returns false
        every { engine.generate(any(), Constants.ORCHESTRATOR_MODEL_ID) } throws RuntimeException("model error")

        val result = classifier.classify(unknown)

        assertTrue(result is ThreatAction.LogOnly || result is ThreatAction.Notify)
    }

    private companion object {
        fun atUtcHour(hour: Int): Long =
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(2025, Calendar.JUNE, 15, hour, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
    }
}
