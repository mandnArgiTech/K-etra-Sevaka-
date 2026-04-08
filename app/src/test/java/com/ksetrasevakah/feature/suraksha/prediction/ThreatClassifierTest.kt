package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.ai.MlcLlmEngine
import com.ksetrasevakah.core.notification.model.TapoEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import com.ksetrasevakah.feature.suraksha.prediction.model.ThreatAction
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ThreatClassifierTest {

    private lateinit var engine: MlcLlmEngine
    private lateinit var spikeDetector: ActivitySpikeDetector
    private lateinit var classifier: ThreatClassifier

    private val personEvent = TapoEvent(
        cameraName = "Front Door",
        eventType = "PERSON",
        timestamp = System.currentTimeMillis(),
        rawTitle = "Front Door: Person detected",
        rawText = "A person was detected"
    )

    private val tamperEvent = TapoEvent(
        cameraName = "Backyard",
        eventType = "TAMPERING",
        timestamp = System.currentTimeMillis(),
        rawTitle = "Backyard: Tampering",
        rawText = "Camera tampering detected"
    )

    @BeforeEach
    fun setup() {
        engine = mockk(relaxed = true)
        spikeDetector = mockk(relaxed = true)
        classifier = ThreatClassifier(engine, spikeDetector)
    }

    @Test
    fun `applyRules returns CriticalAlarm for TAMPERING`() {
        val result = classifier.applyRules(tamperEvent)

        assertTrue(result is ThreatAction.CriticalAlarm)
        assertEquals(ThreatLevel.CRITICAL, (result as ThreatAction.CriticalAlarm).threatLevel)
    }

    @Test
    fun `applyRules returns null for non-tampering events`() {
        val result = classifier.applyRules(personEvent)

        assertEquals(null, result)
    }

    @Test
    fun `classify returns CriticalAlarm for TAMPERING without AI call`() = runTest {
        val result = classifier.classify(tamperEvent)

        assertTrue(result is ThreatAction.CriticalAlarm)
    }

    @Test
    fun `classify uses AI for PERSON events`() = runTest {
        coEvery { spikeDetector.isActivitySpike(any(), any()) } returns false
        every { engine.generate(any(), any()) } returns flowOf("HIGH\nSuspicious person at night")

        val result = classifier.classify(personEvent)

        assertTrue(result is ThreatAction.Notify)
        assertEquals(ThreatLevel.HIGH, (result as ThreatAction.Notify).threatLevel)
    }

    @Test
    fun `parseAiResponse maps CRITICAL to CriticalAlarm`() {
        val result = classifier.parseAiResponse("CRITICAL\nImmediate danger", personEvent)

        assertTrue(result is ThreatAction.CriticalAlarm)
    }

    @Test
    fun `parseAiResponse maps LOW to LogOnly`() {
        val result = classifier.parseAiResponse("LOW\nRoutine activity", personEvent)

        assertTrue(result is ThreatAction.LogOnly)
        assertEquals(ThreatLevel.LOW, (result as ThreatAction.LogOnly).threatLevel)
    }

    @Test
    fun `fallbackClassification returns MEDIUM for PERSON`() {
        val result = classifier.fallbackClassification(personEvent)

        assertTrue(result is ThreatAction.LogOnly)
        assertEquals(ThreatLevel.MEDIUM, (result as ThreatAction.LogOnly).threatLevel)
    }

    @Test
    fun `classify falls back on AI exception`() = runTest {
        coEvery { spikeDetector.isActivitySpike(any(), any()) } returns false
        every { engine.generate(any(), any()) } throws RuntimeException("model error")

        val result = classifier.classify(personEvent)

        assertTrue(result is ThreatAction.LogOnly || result is ThreatAction.Notify)
    }
}
