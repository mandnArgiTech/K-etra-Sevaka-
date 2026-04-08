package com.ksetrasevakah.feature.suraksha.domain.model

enum class EventType {
    PERSON,
    TAMPERING,
    UNKNOWN;

    companion object {
        fun fromString(value: String): EventType =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
    }
}
