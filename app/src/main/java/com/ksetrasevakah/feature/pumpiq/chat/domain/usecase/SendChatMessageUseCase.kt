package com.ksetrasevakah.feature.pumpiq.chat.domain.usecase

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.ChatMessageEntity
import com.ksetrasevakah.core.domain.repository.ChatRepository
import com.ksetrasevakah.feature.pumpiq.chat.domain.model.ChatMessage
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        threadId: Long,
        role: String,
        text: String
    ): Result<Long> {
        val entity = ChatMessageEntity(
            threadId = threadId,
            role = role,
            text = text
        )
        val insertResult = chatRepository.insertMessage(entity)
        if (insertResult is Result.Success) {
            val preview = text.take(80)
            chatRepository.updateThread(
                id = threadId,
                title = "",
                preview = preview,
                lastMessageAt = System.currentTimeMillis()
            )
        }
        return insertResult
    }
}
