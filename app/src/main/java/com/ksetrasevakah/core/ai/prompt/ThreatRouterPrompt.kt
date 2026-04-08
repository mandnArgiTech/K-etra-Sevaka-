package com.ksetrasevakah.core.ai.prompt

import com.ksetrasevakah.core.notification.model.TapoEvent

object ThreatRouterPrompt {

    fun getClassificationPrompt(event: TapoEvent, isActivitySpike: Boolean): String = """
        |You are a security threat classifier for a home/farm surveillance system.
        |Classify the following camera event into a threat level: LOW, MEDIUM, HIGH, or CRITICAL.
        |
        |Camera: ${event.cameraName}
        |Event type: ${event.eventType}
        |Notification title: ${event.rawTitle}
        |Notification text: ${event.rawText}
        |Activity spike detected: $isActivitySpike
        |
        |Rules:
        |- TAMPERING events are always CRITICAL.
        |- Multiple events in a short window (activity spike = true) suggest coordinated activity → upgrade by one level.
        |- PERSON events during night hours (22:00-05:00) are at least MEDIUM.
        |- Single PERSON events during daytime are typically LOW.
        |
        |Respond with exactly two lines:
        |Line 1: The threat level (one of: LOW, MEDIUM, HIGH, CRITICAL)
        |Line 2: A brief one-sentence explanation.
    """.trimMargin()
}
