package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.VectorDocumentEntity

@Dao
interface VectorDocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: VectorDocumentEntity): Long

    @Query("SELECT * FROM vector_documents ORDER BY id ASC")
    suspend fun getAll(): List<VectorDocumentEntity>

    @Query("DELETE FROM vector_documents")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM vector_documents")
    suspend fun count(): Long

    @Query("SELECT MAX(createdAt) FROM vector_documents")
    suspend fun maxCreatedAt(): Long?

    @Query("SELECT MIN(createdAt) FROM vector_documents")
    suspend fun minCreatedAt(): Long?

    @Query("SELECT * FROM vector_documents ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<VectorDocumentEntity>
}
