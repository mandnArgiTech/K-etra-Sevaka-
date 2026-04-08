package com.ksetrasevakah.feature.pumpiq.chat.model

import com.ksetrasevakah.feature.pumpiq.chat.domain.model.ChatMessage
import com.ksetrasevakah.feature.pumpiq.chat.domain.model.ChatThread

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val currentThreadId: Long? = null,
    val threads: List<ChatThread> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
