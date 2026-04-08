package com.ksetrasevakah.core.ai.prompt

object IngestionPrompt {

    fun getExtractionPrompt(smsBody: String): String = """
        |You are a telemetry extraction assistant for an agricultural motor monitoring system.
        |Extract structured data from the following SMS message sent by a motor control panel.
        |
        |SMS: "$smsBody"
        |
        |Return ONLY a JSON object with these fields (use null for missing values):
        |{
        |  "motorOn": boolean,
        |  "phaseR": float or null,
        |  "phaseY": float or null,
        |  "phaseB": float or null,
        |  "voltage": float or null,
        |  "temperature": float or null,
        |  "runtimeMinutes": int or null
        |}
    """.trimMargin()
}
