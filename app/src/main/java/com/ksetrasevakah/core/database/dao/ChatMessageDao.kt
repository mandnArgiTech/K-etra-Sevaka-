package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE thread_id = :threadId ORDER BY created_at ASC")
    fun getMessagesForThread(threadId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE thread_id = :threadId ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessagesPaginated(threadId: Long, limit: Int, offset: Int): List<ChatMessageEntity>

    @Query("SELECT * FROM chat_messages WHERE thread_id = :threadId ORDER BY created_at DESC LIMIT :limit")
    suspend fun getRecentMessages(threadId: Long, limit: Int): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE thread_id = :threadId")
    suspend fun deleteForThread(threadId: Long)
}
