package com.ksetrasevakah.core.ai.prompt

import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel

object SecurityBriefingPrompt {

    fun getBriefingPrompt(
        events: List<SecurityEvent>,
        threatCounts: Map<ThreatLevel, Int>
    ): String = """
        |You are a concise farm security analyst.
        |Given the following security events and threat summary, produce a 2-3 sentence
        |security briefing for the farm owner. Highlight critical threats and recommend
        |immediate actions if needed.
        |
        |Threat summary:
        |  Critical: ${threatCounts[ThreatLevel.CRITICAL] ?: 0}
        |  High: ${threatCounts[ThreatLevel.HIGH] ?: 0}
        |  Medium: ${threatCounts[ThreatLevel.MEDIUM] ?: 0}
        |  Low: ${threatCounts[ThreatLevel.LOW] ?: 0}
        |
        |Recent events (last ${events.size}):
        |${events.take(10).joinToString("\n") { "  - ${it.eventType.name}: ${it.description} [${it.threatLevel}]" }}
        |
        |Respond with ONLY the briefing text, no JSON or formatting.
    """.trimMargin()
}
