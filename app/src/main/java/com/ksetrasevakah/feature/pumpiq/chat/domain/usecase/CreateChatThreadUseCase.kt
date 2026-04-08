package com.ksetrasevakah.feature.pumpiq.chat.domain.usecase

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.ChatRepository
import javax.inject.Inject

class CreateChatThreadUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(title: String = "New Chat"): Result<Long> =
        chatRepository.createThread(title)
}
