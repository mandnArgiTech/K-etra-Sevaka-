package com.ksetrasevakah.feature.suraksha.prediction

import com.ksetrasevakah.core.ai.MlcLlmEngine
import com.ksetrasevakah.core.ai.prompt.ThreatRouterPrompt
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.notification.model.TapoEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import com.ksetrasevakah.feature.suraksha.prediction.model.ThreatAction
import kotlinx.coroutines.flow.fold
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreatClassifier @Inject constructor(
    private val engine: MlcLlmEngine,
    private val spikeDetector: ActivitySpikeDetector
) {

    suspend fun classify(event: TapoEvent): ThreatAction {
        val ruleResult = applyRules(event)
        if (ruleResult != null) return ruleResult

        return classifyWithAi(event)
    }

    internal suspend fun applyRules(event: TapoEvent): ThreatAction? {
        val eventType = event.eventType.uppercase()

        if (eventType == "TAMPERING") {
            return ThreatAction.CriticalAlarm(
                threatLevel = ThreatLevel.CRITICAL,
                confidence = 0.95f,
                summary = "Camera tampering detected on ${event.cameraName}",
                title = "CRITICAL: Camera Tampering",
                cameraName = event.cameraName
            )
        }

        if (eventType == "PERSON") {
            val calendar = Calendar.getInstance().apply { timeInMillis = event.timestamp }
            val hour = calendar.get(Calendar.HOUR_OF_DAY)

            var level = when (hour) {
                in 6..17 -> ThreatLevel.LOW
                in 18..21 -> ThreatLevel.MEDIUM
                else -> ThreatLevel.HIGH
            }

            val isSpike = spikeDetector.isActivitySpike(event.cameraName, event.timestamp)
            if (isSpike) {
                level = level.elevate()
            }

            if (hour !in 6..21) {
                val isCoordinated = spikeDetector.isCoordinatedActivity(event.timestamp)
                if (isCoordinated) {
                    level = ThreatLevel.CRITICAL
                }
            }

            val summary = buildPersonSummary(event.cameraName, hour, level, isSpike)

            return when {
                level.isCritical -> ThreatAction.CriticalAlarm(
                    threatLevel = level,
                    confidence = 0.9f,
                    summary = summary,
                    title = "CRITICAL: Coordinated Intrusion",
                    cameraName = event.cameraName
                )
                level.isAlertable -> ThreatAction.Notify(
                    threatLevel = level,
                    confidence = 0.85f,
                    summary = summary,
                    title = "${level.name}: Person at ${event.cameraName}"
                )
                else -> ThreatAction.LogOnly(
                    threatLevel = level,
                    confidence = 0.9f,
                    summary = summary
                )
            }
        }

        return null
    }

    private fun buildPersonSummary(
        cameraName: String,
        hour: Int,
        level: ThreatLevel,
        isSpike: Boolean
    ): String {
        val spikeNote = if (isSpike) " Activity spike." else ""
        return "Person at $cameraName (hour=$hour) — ${level.name}.$spikeNote"
    }

    internal suspend fun classifyWithAi(event: TapoEvent): ThreatAction {
        return try {
            val isSpike = spikeDetector.isActivitySpike(event.cameraName, event.timestamp)
            val prompt = ThreatRouterPrompt.getClassificationPrompt(event, isSpike)
            val response = engine.generate(prompt, Constants.INGESTION_MODEL_ID)
                .fold(StringBuilder()) { acc, token -> acc.append(token) }
                .toString()
                .trim()

            parseAiResponse(response, event)
        } catch (_: Exception) {
            fallbackClassification(event)
        }
    }

    internal fun parseAiResponse(response: String, event: TapoEvent): ThreatAction {
        val lower = response.lowercase()
        val threatLevel = when {
            lower.contains("critical") -> ThreatLevel.CRITICAL
            lower.contains("high") -> ThreatLevel.HIGH
            lower.contains("medium") -> ThreatLevel.MEDIUM
            else -> ThreatLevel.LOW
        }

        val summary = response.lines().lastOrNull { it.isNotBlank() } ?: response

        return when {
            threatLevel.isCritical -> ThreatAction.CriticalAlarm(
                threatLevel = threatLevel,
                confidence = 0.8f,
                summary = summary,
                title = "CRITICAL: ${event.eventType} detected",
                cameraName = event.cameraName
            )
            threatLevel.isAlertable -> ThreatAction.Notify(
                threatLevel = threatLevel,
                confidence = 0.7f,
                summary = summary,
                title = "${threatLevel.name}: ${event.eventType} on ${event.cameraName}"
            )
            else -> ThreatAction.LogOnly(
                threatLevel = threatLevel,
                confidence = 0.6f,
                summary = summary
            )
        }
    }

    internal fun fallbackClassification(event: TapoEvent): ThreatAction {
        val threatLevel = when (event.eventType.uppercase()) {
            "PERSON" -> ThreatLevel.MEDIUM
            "TAMPERING" -> ThreatLevel.CRITICAL
            else -> ThreatLevel.LOW
        }

        val summary = "${event.eventType} detected on ${event.cameraName}"

        return if (threatLevel.isAlertable) {
            ThreatAction.Notify(
                threatLevel = threatLevel,
                confidence = 0.5f,
                summary = summary,
                title = "${threatLevel.name}: $summary"
            )
        } else {
            ThreatAction.LogOnly(
                threatLevel = threatLevel,
                confidence = 0.5f,
                summary = summary
            )
        }
    }
}
