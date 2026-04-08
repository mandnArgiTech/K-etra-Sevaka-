package com.ksetrasevakah.core.ai.prompt

import com.ksetrasevakah.core.database.entity.TelemetryEntity

object NarrativePrompt {

    fun getNarrativePrompt(telemetry: TelemetryEntity): String = """
        |You are a concise agricultural motor health narrator.
        |Given the following telemetry reading, produce a 1-2 sentence human-readable narrative
        |describing the motor's current state, any anomalies, and recommended actions if needed.
        |
        |Motor status: ${if (telemetry.motorOn) "ON" else "OFF"}
        |Phase R current: ${telemetry.phaseR ?: "N/A"} A
        |Phase Y current: ${telemetry.phaseY ?: "N/A"} A
        |Phase B current: ${telemetry.phaseB ?: "N/A"} A
        |Voltage: ${telemetry.voltage ?: "N/A"} V
        |Temperature: ${telemetry.temperature ?: "N/A"} °C
        |Runtime: ${telemetry.runtimeMinutes ?: "N/A"} minutes
        |
        |Respond with ONLY the narrative text, no JSON or formatting.
    """.trimMargin()
}
