package com.ksetrasevakah.feature.pumpiq.chat.domain.model

data class ChatMessage(
    val id: Long,
    val threadId: Long,
    val role: String,
    val text: String,
    val createdAt: Long
) {
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
        const val ROLE_SYSTEM = "system"
    }
}
