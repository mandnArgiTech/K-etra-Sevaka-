package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.ChatThreadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatThreadDao {
    @Query("SELECT * FROM chat_threads ORDER BY last_message_at DESC")
    fun getAll(): Flow<List<ChatThreadEntity>>

    @Query("SELECT * FROM chat_threads WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ChatThreadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChatThreadEntity): Long

    @Query("UPDATE chat_threads SET title = :title, preview = :preview, last_message_at = :lastMessageAt WHERE id = :id")
    suspend fun update(id: Long, title: String, preview: String, lastMessageAt: Long)

    @Query("DELETE FROM chat_threads WHERE id = :id")
    suspend fun delete(id: Long)
}
