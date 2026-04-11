package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.BackupLogEntity

@Dao
interface BackupLogDao {
    @Query("SELECT * FROM backup_log ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 10): List<BackupLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BackupLogEntity): Long

    @Query("SELECT COUNT(*) FROM backup_log")
    suspend fun count(): Long
}
