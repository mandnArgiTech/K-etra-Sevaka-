package com.ksetrasevakah.core.notification

import com.ksetrasevakah.core.notification.model.TapoEvent

object TapoNotificationParser {

    private val CAMERA_REGEX_TITLE = Regex("""^(.+?):\s""")
    private val CAMERA_REGEX_BODY = Regex("""\b(?:from|on|at)\s+['"]?([^'",.!]+)['"]?""", RegexOption.IGNORE_CASE)
    private val CAMERA_REGEX_BRACKET = Regex("""\[(.+?)]""")

    private val EVENT_TYPE_MAP = mapOf(
        "person" to "PERSON",
        "someone" to "PERSON",
        "human" to "PERSON",
        "motion" to "PERSON",
        "people" to "PERSON",
        "tamper" to "TAMPERING",
        "tampering" to "TAMPERING",
        "camera covered" to "TAMPERING",
        "lens blocked" to "TAMPERING"
    )

    fun parse(title: String, text: String, originTimestamp: Long, sbnKey: String): TapoEvent {
        val cameraName = extractCameraName(title, text)
        val eventType = extractEventType(title, text)
        return TapoEvent(
            cameraName = cameraName,
            eventType = eventType,
            timestamp = originTimestamp,
            rawTitle = title,
            rawText = text,
            sbnKey = sbnKey
        )
    }

    internal fun extractCameraName(title: String, text: String): String {
        CAMERA_REGEX_TITLE.find(title)?.groupValues?.get(1)?.let { name ->
            if (name.isNotBlank()) return name.trim()
        }

        CAMERA_REGEX_BRACKET.find(text)?.groupValues?.get(1)?.let { name ->
            if (name.isNotBlank()) return name.trim()
        }

        CAMERA_REGEX_BODY.find(text)?.groupValues?.get(1)?.let { name ->
            if (name.isNotBlank()) return name.trim()
        }

        return "unknown_camera"
    }

    internal fun extractEventType(title: String, text: String): String {
        val combined = "$title $text".lowercase()
        for ((keyword, type) in EVENT_TYPE_MAP) {
            if (combined.contains(keyword)) return type
        }
        return "UNKNOWN"
    }
}
