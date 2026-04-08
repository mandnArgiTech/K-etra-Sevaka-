package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.SecurityBriefingEntity

@Dao
interface SecurityBriefingDao {

    @Query("SELECT * FROM security_briefings ORDER BY generated_at DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 10): List<SecurityBriefingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SecurityBriefingEntity): Long
}
