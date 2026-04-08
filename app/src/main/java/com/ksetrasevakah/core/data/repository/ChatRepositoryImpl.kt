package com.ksetrasevakah.core.data.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.ChatMessageDao
import com.ksetrasevakah.core.database.dao.ChatThreadDao
import com.ksetrasevakah.core.database.entity.ChatMessageEntity
import com.ksetrasevakah.core.database.entity.ChatThreadEntity
import com.ksetrasevakah.core.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val threadDao: ChatThreadDao,
    private val messageDao: ChatMessageDao,
) : ChatRepository {

    override fun observeThreads(): Flow<Result<List<ChatThreadEntity>>> =
        threadDao.getAll().map { list ->
            try {
                Result.Success(list)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to observe threads", e)
            }
        }

    override suspend fun getThreadById(id: Long): Result<ChatThreadEntity?> =
        try {
            Result.Success(threadDao.getById(id))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get thread", e)
        }

    override suspend fun createThread(title: String): Result<Long> =
        try {
            val entity = ChatThreadEntity(title = title)
            Result.Success(threadDao.insert(entity))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to create thread", e)
        }

    override suspend fun updateThread(
        id: Long,
        title: String,
        preview: String,
        lastMessageAt: Long,
    ): Result<Unit> =
        try {
            threadDao.update(id, title, preview, lastMessageAt)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update thread", e)
        }

    override suspend fun deleteThread(id: Long): Result<Unit> =
        try {
            threadDao.delete(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete thread", e)
        }

    override fun observeMessages(threadId: Long): Flow<Result<List<ChatMessageEntity>>> =
        messageDao.getMessagesForThread(threadId).map { list ->
            try {
                Result.Success(list)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to observe messages", e)
            }
        }

    override suspend fun getRecentMessages(threadId: Long, limit: Int): Result<List<ChatMessageEntity>> =
        try {
            Result.Success(messageDao.getRecentMessages(threadId, limit))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get recent messages", e)
        }

    override suspend fun insertMessage(entity: ChatMessageEntity): Result<Long> =
        try {
            Result.Success(messageDao.insert(entity))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to insert message", e)
        }
}
