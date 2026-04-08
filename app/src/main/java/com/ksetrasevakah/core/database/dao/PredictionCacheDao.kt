package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.PredictionCacheEntity

@Dao
interface PredictionCacheDao {
    @Query("SELECT * FROM prediction_cache WHERE type = :type LIMIT 1")
    suspend fun getByType(type: String): PredictionCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PredictionCacheEntity)

    @Query("DELETE FROM prediction_cache WHERE type = :type")
    suspend fun deleteByType(type: String)
}
