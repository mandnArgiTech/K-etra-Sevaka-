package com.ksetrasevakah.feature.pumpiq.chat.domain.usecase

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.ChatRepository
import com.ksetrasevakah.feature.pumpiq.chat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(threadId: Long): Flow<Result<List<ChatMessage>>> =
        chatRepository.observeMessages(threadId).map { result ->
            when (result) {
                is Result.Success -> Result.Success(
                    result.data.map { entity ->
                        ChatMessage(
                            id = entity.id,
                            threadId = entity.threadId,
                            role = entity.role,
                            text = entity.text,
                            createdAt = entity.createdAt
                        )
                    }
                )
                is Result.Error -> Result.Error(result.message, result.throwable)
                is Result.Loading -> Result.Loading
            }
        }
}
