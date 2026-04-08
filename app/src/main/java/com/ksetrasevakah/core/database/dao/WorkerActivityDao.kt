package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerActivityDao {
    @Query("SELECT * FROM worker_activity ORDER BY date DESC LIMIT 14")
    fun getLast14Days(): Flow<List<WorkerActivityEntity>>

    @Query("SELECT * FROM worker_activity ORDER BY date DESC LIMIT :days")
    suspend fun getLastNDays(days: Int = 14): List<WorkerActivityEntity>

    @Query("SELECT AVG(on_time) FROM worker_activity WHERE on_time IS NOT NULL ORDER BY date DESC LIMIT :days")
    suspend fun getAvgOnTime(days: Int = 14): Long?

    @Query("SELECT COUNT(*) FROM worker_activity WHERE forgot_off = 1 AND date >= :since")
    suspend fun getForgotOffCount(since: String): Int

    @Query("SELECT * FROM worker_activity WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): WorkerActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WorkerActivityEntity)
}
