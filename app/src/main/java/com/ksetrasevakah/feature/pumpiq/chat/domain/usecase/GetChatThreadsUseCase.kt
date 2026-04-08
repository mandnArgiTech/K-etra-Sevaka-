package com.ksetrasevakah.feature.pumpiq.chat.domain.usecase

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.ChatRepository
import com.ksetrasevakah.feature.pumpiq.chat.domain.model.ChatThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChatThreadsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<Result<List<ChatThread>>> =
        chatRepository.observeThreads().map { result ->
            when (result) {
                is Result.Success -> Result.Success(
                    result.data.map { entity ->
                        ChatThread(
                            id = entity.id,
                            title = entity.title,
                            preview = entity.preview,
                            createdAt = entity.createdAt,
                            lastMessageAt = entity.lastMessageAt
                        )
                    }
                )
                is Result.Error -> Result.Error(result.message, result.throwable)
                is Result.Loading -> Result.Loading
            }
        }
}
