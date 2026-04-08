package com.ksetrasevakah.feature.suraksha.domain.model

enum class ThreatLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    val isAlertable: Boolean
        get() = this == HIGH || this == CRITICAL

    val isCritical: Boolean
        get() = this == CRITICAL

    companion object {
        fun fromString(value: String): ThreatLevel =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: LOW
    }
}
