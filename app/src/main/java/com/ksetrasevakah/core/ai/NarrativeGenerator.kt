package com.ksetrasevakah.core.ai

import com.ksetrasevakah.core.ai.prompt.NarrativePrompt
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import kotlinx.coroutines.flow.fold
import javax.inject.Inject

class NarrativeGenerator @Inject constructor(
    private val engine: MlcLlmEngine
) {

    suspend fun generate(telemetry: TelemetryEntity): Result<String> {
        return try {
            val prompt = NarrativePrompt.getNarrativePrompt(telemetry)
            val narrative = engine.generate(prompt, Constants.INGESTION_MODEL_ID)
                .fold(StringBuilder()) { acc, token -> acc.append(token) }
                .toString()
                .trim()

            if (narrative.isBlank()) {
                Result.Error("Model returned empty narrative")
            } else {
                Result.Success(narrative)
            }
        } catch (e: Exception) {
            Result.Error("Narrative generation failed: ${e.message}", e)
        }
    }
}
