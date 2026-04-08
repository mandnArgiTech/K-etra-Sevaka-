package com.ksetrasevakah.core.ai.parser

import com.ksetrasevakah.core.common.Result

data class ParsedTelemetry(
    val motorOn: Boolean,
    val phaseR: Float? = null,
    val phaseY: Float? = null,
    val phaseB: Float? = null,
    val voltage: Float? = null,
    val temperature: Float? = null,
    val runtimeMinutes: Int? = null
)

object TelemetryParser {

    fun parseResponse(json: String): Result<ParsedTelemetry> {
        val jsonBlock = extractJsonBlock(json)
        if (jsonBlock != null) {
            val parsed = parseJsonFields(jsonBlock)
            if (parsed != null) return Result.Success(parsed)
        }
        return tryRegexFallback(json)
    }

    private fun extractJsonBlock(text: String): String? {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start == -1 || end == -1 || end <= start) return null
        return text.substring(start, end + 1)
    }

    private fun parseJsonFields(json: String): ParsedTelemetry? {
        return try {
            val motorOn = extractJsonBoolean(json, "motorOn") ?: false
            val phaseR = extractJsonFloat(json, "phaseR")
            val phaseY = extractJsonFloat(json, "phaseY")
            val phaseB = extractJsonFloat(json, "phaseB")
            val voltage = extractJsonFloat(json, "voltage")
            val temperature = extractJsonFloat(json, "temperature")
            val runtimeMinutes = extractJsonInt(json, "runtimeMinutes")

            ParsedTelemetry(
                motorOn = motorOn,
                phaseR = phaseR,
                phaseY = phaseY,
                phaseB = phaseB,
                voltage = voltage,
                temperature = temperature,
                runtimeMinutes = runtimeMinutes
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun extractJsonBoolean(json: String, key: String): Boolean? {
        val pattern = Regex(""""$key"\s*:\s*(true|false)""")
        return pattern.find(json)?.groupValues?.get(1)?.toBooleanStrictOrNull()
    }

    private fun extractJsonFloat(json: String, key: String): Float? {
        val nullPattern = Regex(""""$key"\s*:\s*null""")
        if (nullPattern.containsMatchIn(json)) return null
        val pattern = Regex(""""$key"\s*:\s*(-?\d+\.?\d*)""")
        return pattern.find(json)?.groupValues?.get(1)?.toFloatOrNull()
    }

    private fun extractJsonInt(json: String, key: String): Int? {
        val nullPattern = Regex(""""$key"\s*:\s*null""")
        if (nullPattern.containsMatchIn(json)) return null
        val pattern = Regex(""""$key"\s*:\s*(-?\d+)""")
        return pattern.find(json)?.groupValues?.get(1)?.toIntOrNull()
    }

    internal fun tryRegexFallback(text: String): Result<ParsedTelemetry> {
        val upper = text.uppercase()
        val motorOn = upper.contains("MOTOR ON") || upper.contains("STATUS: ON")

        val phaseR = extractFloat(text, PHASE_R_PATTERN)
        val phaseY = extractFloat(text, PHASE_Y_PATTERN)
        val phaseB = extractFloat(text, PHASE_B_PATTERN)
        val voltage = extractFloat(text, VOLTAGE_PATTERN)
        val temperature = extractFloat(text, TEMPERATURE_PATTERN)
        val runtime = extractInt(text, RUNTIME_PATTERN)

        if (phaseR == null && phaseY == null && phaseB == null &&
            voltage == null && temperature == null && runtime == null
        ) {
            return Result.Error("Unable to parse telemetry from response")
        }

        return Result.Success(
            ParsedTelemetry(
                motorOn = motorOn,
                phaseR = phaseR,
                phaseY = phaseY,
                phaseB = phaseB,
                voltage = voltage,
                temperature = temperature,
                runtimeMinutes = runtime
            )
        )
    }

    private fun extractFloat(text: String, pattern: Regex): Float? =
        pattern.find(text)?.groupValues?.getOrNull(1)?.toFloatOrNull()

    private fun extractInt(text: String, pattern: Regex): Int? =
        pattern.find(text)?.groupValues?.getOrNull(1)?.toIntOrNull()

    private val PHASE_R_PATTERN = Regex("""[Rr]\s*[=:]\s*(\d+\.?\d*)""")
    private val PHASE_Y_PATTERN = Regex("""[Yy]\s*[=:]\s*(\d+\.?\d*)""")
    private val PHASE_B_PATTERN = Regex("""[Bb]\s*[=:]\s*(\d+\.?\d*)""")
    private val VOLTAGE_PATTERN = Regex("""[Vv](?:oltage)?\s*[=:]\s*(\d+\.?\d*)""")
    private val TEMPERATURE_PATTERN = Regex("""[Tt](?:emp(?:erature)?)?\s*[=:]\s*(\d+\.?\d*)""")
    private val RUNTIME_PATTERN = Regex("""[Rr]un(?:time)?\s*[=:]\s*(\d+)""")
}
