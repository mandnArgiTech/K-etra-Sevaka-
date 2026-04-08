package com.ksetrasevakah.core.domain.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.ChatMessageEntity
import com.ksetrasevakah.core.database.entity.ChatThreadEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeThreads(): Flow<Result<List<ChatThreadEntity>>>
    suspend fun getThreadById(id: Long): Result<ChatThreadEntity?>
    suspend fun createThread(title: String): Result<Long>
    suspend fun updateThread(id: Long, title: String, preview: String, lastMessageAt: Long): Result<Unit>
    suspend fun deleteThread(id: Long): Result<Unit>
    fun observeMessages(threadId: Long): Flow<Result<List<ChatMessageEntity>>>
    suspend fun getRecentMessages(threadId: Long, limit: Int): Result<List<ChatMessageEntity>>
    suspend fun insertMessage(entity: ChatMessageEntity): Result<Long>
}
