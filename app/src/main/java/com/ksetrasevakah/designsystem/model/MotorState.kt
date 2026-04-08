package com.ksetrasevakah.designsystem.model

/**
 * Represents the current motor operational state.
 * Drives UI styling, button labels, and state machine transitions.
 */
enum class MotorState {
    OFF, PENDING_START, ON, PENDING_STOP;

    val isOff get() = this == OFF
    val isOn get() = this == ON
    val isPending get() = this == PENDING_START || this == PENDING_STOP
    val displayLabel: String
        get() = when (this) {
            OFF -> "Offline"
            PENDING_START -> "Starting..."
            ON -> "Running"
            PENDING_STOP -> "Stopping..."
        }
}
