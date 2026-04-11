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

    @Query("SELECT COUNT(*) FROM telemetry_log")
    suspend fun count(): Long

    @Query("SELECT MAX(timestamp) FROM telemetry_log")
    suspend fun maxTimestamp(): Long?

    @Query("SELECT MIN(timestamp) FROM telemetry_log")
    suspend fun minTimestamp(): Long?

    @Query("SELECT * FROM telemetry_log ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentPreview(limit: Int): List<TelemetryEntity>
}
