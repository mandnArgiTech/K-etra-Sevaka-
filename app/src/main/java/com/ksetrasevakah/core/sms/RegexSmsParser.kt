package com.ksetrasevakah.core.sms

import com.ksetrasevakah.core.ai.parser.ParsedTelemetry

/**
 * Deterministic, zero-latency parser for pump-panel SMS messages.
 * Replaces the 0.5 B LLM ingestion path; runs in < 5 ms on any device.
 * Patterns cover the common R/Y/B (or L1/L2/L3) phase notation used by
 * Indian panel controllers as well as generic Voltage / Temperature / Runtime fields.
 */
object RegexSmsParser {

    fun parse(smsBody: String): ParsedTelemetry {
        val upper = smsBody.uppercase()
        val motorOn = upper.contains("MOTOR ON") ||
            upper.contains("MOTOR STARTED") ||
            upper.contains("STARTED") ||
            upper.contains("STATUS: ON") ||
            upper.contains("PUMP ON")

        val phaseR  = PHASE_R.find(smsBody)?.groupValues?.getOrNull(1)?.toFloatOrNull()
        val phaseY  = PHASE_Y.find(smsBody)?.groupValues?.getOrNull(1)?.toFloatOrNull()
        val phaseB  = PHASE_B.find(smsBody)?.groupValues?.getOrNull(1)?.toFloatOrNull()
        val voltage = VOLTAGE.find(smsBody)?.groupValues?.getOrNull(1)?.toFloatOrNull()
        val temp    = TEMPERATURE.find(smsBody)?.groupValues?.getOrNull(1)?.toFloatOrNull()
        val runtime = RUNTIME.find(smsBody)?.groupValues?.getOrNull(1)?.toIntOrNull()

        return ParsedTelemetry(
            motorOn        = motorOn,
            phaseR         = phaseR,
            phaseY         = phaseY,
            phaseB         = phaseB,
            voltage        = voltage,
            temperature    = temp,
            runtimeMinutes = runtime
        )
    }

    private val PHASE_R     = Regex("""(?:R|L1)\s*[=:]\s*([\d.]+)\s*A""",     RegexOption.IGNORE_CASE)
    private val PHASE_Y     = Regex("""(?:Y|L2)\s*[=:]\s*([\d.]+)\s*A""",     RegexOption.IGNORE_CASE)
    private val PHASE_B     = Regex("""(?:B|L3)\s*[=:]\s*([\d.]+)\s*A""",     RegexOption.IGNORE_CASE)
    private val VOLTAGE     = Regex("""V(?:oltage)?\s*[=:]\s*([\d.]+)""",      RegexOption.IGNORE_CASE)
    private val TEMPERATURE = Regex("""T(?:emp(?:erature)?)?\s*[=:]\s*([\d.]+)""", RegexOption.IGNORE_CASE)
    private val RUNTIME     = Regex("""(\d+)\s*min(?:utes?)?""",               RegexOption.IGNORE_CASE)
}
