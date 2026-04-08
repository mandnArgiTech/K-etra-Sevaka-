package com.ksetrasevakah.feature.pumpiq.chat.prompt

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.ChatRepository
import javax.inject.Inject

class ContextAssembler @Inject constructor(
    private val chatRepository: ChatRepository
) {

    suspend fun assemble(threadId: Long): String {
        val recentMessages = when (
            val result = chatRepository.getRecentMessages(threadId, Constants.MAX_CHAT_CONTEXT_MESSAGES)
        ) {
            is Result.Success -> result.data
            else -> return ""
        }

        if (recentMessages.isEmpty()) return ""

        return buildString {
            appendLine("Previous conversation:")
            recentMessages.sortedBy { it.createdAt }.forEach { msg ->
                val roleLabel = when (msg.role) {
                    "user" -> "User"
                    "assistant" -> "Assistant"
                    else -> msg.role
                }
                appendLine("$roleLabel: ${msg.text}")
            }
        }
    }
}
