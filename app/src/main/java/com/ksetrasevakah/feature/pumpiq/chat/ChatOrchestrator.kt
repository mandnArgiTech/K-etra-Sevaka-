package com.ksetrasevakah.feature.pumpiq.chat

import com.ksetrasevakah.core.ai.MlcLlmEngine
import com.ksetrasevakah.core.ai.ModelManager
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.ChatRepository
import com.ksetrasevakah.feature.pumpiq.chat.prompt.ContextAssembler
import com.ksetrasevakah.feature.pumpiq.chat.prompt.SystemPromptBuilder
import kotlinx.coroutines.flow.fold
import javax.inject.Inject

class ChatOrchestrator @Inject constructor(
    private val engine: MlcLlmEngine,
    private val modelManager: ModelManager,
    private val chatRepository: ChatRepository,
    private val systemPromptBuilder: SystemPromptBuilder,
    private val contextAssembler: ContextAssembler
) {

    suspend fun getResponse(threadId: Long, userMessage: String): Result<String> {
        return try {
            modelManager.loadOrchestratorModel()

            val ragQuery = userMessage.trim().take(Constants.RAG_QUERY_MAX_CHARS)
            val systemPrompt = systemPromptBuilder.build(ragQuery = ragQuery)
            val context = contextAssembler.assemble(threadId)
            val fullPrompt = buildString {
                appendLine(systemPrompt)
                appendLine()
                appendLine(context)
                appendLine()
                appendLine("User: $userMessage")
                appendLine("Assistant:")
            }

            val response = engine.generate(fullPrompt, Constants.ORCHESTRATOR_MODEL_ID)
                .fold(StringBuilder()) { acc, token -> acc.append(token) }
                .toString()
                .trim()

            modelManager.releaseOrchestratorIfIdle()

            if (response.isBlank()) {
                Result.Error("Model returned empty response")
            } else {
                Result.Success(response)
            }
        } catch (e: Exception) {
            Result.Error("Chat generation failed: ${e.message}", e)
        }
    }
}
