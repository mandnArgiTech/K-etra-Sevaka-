package com.ksetrasevakah.designsystem.model

/**
 * Threat/risk classification used by prediction cards and Suraksha module.
 */
enum class RiskLevel {
    LOW, MEDIUM, HIGH, CRITICAL;

    companion object {
        fun fromPercentage(pct: Int): RiskLevel = when {
            pct < 25 -> LOW
            pct < 50 -> MEDIUM
            pct < 75 -> HIGH
            else -> CRITICAL
        }
    }
}
