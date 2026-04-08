package com.ksetrasevakah.feature.pumpiq.chat.prompt

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.core.domain.repository.PredictionRepository
import com.ksetrasevakah.core.vectorstore.RagPipeline
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SystemPromptBuilder @Inject constructor(
    private val motorStateRepository: MotorStateRepository,
    private val predictionRepository: PredictionRepository,
    private val ragPipeline: RagPipeline
) {

    suspend fun build(ragQuery: String = ""): String {
        val motorState = try {
            val result = motorStateRepository.observeMotorState().first()
            if (result is Result.Success) result.data.displayLabel else "UNKNOWN"
        } catch (_: Exception) {
            "UNKNOWN"
        }

        val prediction = when (val result = predictionRepository.getByType("failure")) {
            is Result.Success -> result.data?.let {
                "Prediction: ${it.type} - confidence ${it.confidence}"
            } ?: "No predictions available"
            else -> "Predictions unavailable"
        }

        val ragContext = if (ragQuery.isNotBlank()) {
            when (val result = ragPipeline.query(ragQuery)) {
                is Result.Success -> result.data
                else -> ""
            }
        } else ""

        return buildString {
            appendLine("You are PumpIQ, an AI assistant for agricultural motor pump management.")
            appendLine("You help farmers monitor, maintain, and optimize their water pump systems.")
            appendLine()
            appendLine("Current Motor State: $motorState")
            appendLine(prediction)
            if (ragContext.isNotBlank()) {
                appendLine()
                appendLine("Relevant context from knowledge base:")
                appendLine(ragContext)
            }
            appendLine()
            appendLine("Provide concise, actionable advice. Use simple language suitable for farmers.")
            appendLine("If data is insufficient, say so rather than guessing.")
        }
    }
}
