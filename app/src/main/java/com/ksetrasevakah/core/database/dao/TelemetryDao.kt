package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TelemetryDao {
    @Query("SELECT * FROM telemetry_log WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getRecent(since: Long): Flow<List<TelemetryEntity>>

    @Query("SELECT * FROM telemetry_log WHERE timestamp >= :since ORDER BY timestamp ASC")
    suspend fun getRecentList(since: Long): List<TelemetryEntity>

    @Query("SELECT AVG(voltage) FROM telemetry_log WHERE timestamp >= :since AND timestamp < :until")
    suspend fun getAvgVoltage(since: Long, until: Long): Float?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TelemetryEntity): Long
}
