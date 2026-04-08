package com.ksetrasevakah.feature.suraksha.domain.model

enum class CameraMode {
    ACTIVE,
    SILENT,
    DROP;

    val shouldProcess: Boolean
        get() = this != DROP

    val shouldAlert: Boolean
        get() = this == ACTIVE

    companion object {
        fun fromString(value: String): CameraMode =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: ACTIVE
    }
}
