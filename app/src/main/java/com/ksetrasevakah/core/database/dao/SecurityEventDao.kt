package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.SecurityEventEntity
import com.ksetrasevakah.core.database.model.CameraCount
import com.ksetrasevakah.core.database.model.HourlyCount
import com.ksetrasevakah.core.database.model.ThreatCount
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityEventDao {

    @Query(
        "SELECT * FROM security_events WHERE origin_timestamp >= :since " +
            "ORDER BY origin_timestamp DESC LIMIT :limit"
    )
    fun getRecentEvents(since: Long, limit: Int = 50): Flow<List<SecurityEventEntity>>

    @Query(
        "SELECT threat_level, COUNT(*) as count FROM security_events " +
            "WHERE origin_timestamp >= :since GROUP BY threat_level"
    )
    suspend fun getThreatDistribution(since: Long): List<ThreatCount>

    @Query(
        "SELECT hour_of_day, COUNT(*) as count FROM security_events " +
            "WHERE origin_timestamp >= :since GROUP BY hour_of_day ORDER BY hour_of_day"
    )
    suspend fun getHourlyHeatmap(since: Long): List<HourlyCount>

    @Query(
        "SELECT camera_name, COUNT(*) as count FROM security_events " +
            "WHERE origin_timestamp >= :windowStart AND origin_timestamp <= :windowEnd " +
            "GROUP BY camera_name ORDER BY count DESC"
    )
    suspend fun getActivitySpikes(windowStart: Long, windowEnd: Long): List<CameraCount>

    @Query(
        "SELECT * FROM security_events WHERE camera_name = :cameraName " +
            "ORDER BY origin_timestamp DESC LIMIT :limit"
    )
    suspend fun getEventsForCamera(cameraName: String, limit: Int = 20): List<SecurityEventEntity>

    @Query(
        "SELECT COUNT(*) FROM security_events WHERE threat_level IN ('HIGH', 'CRITICAL') " +
            "AND acknowledged = 0"
    )
    suspend fun getUnacknowledgedHighCount(): Int

    @Query(
        "SELECT * FROM security_events WHERE origin_timestamp >= :since " +
            "ORDER BY origin_timestamp DESC"
    )
    suspend fun getRecentEventsList(since: Long): List<SecurityEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SecurityEventEntity): Long

    @Query("UPDATE security_events SET acknowledged = 1 WHERE id = :eventId")
    suspend fun acknowledge(eventId: Long)
}
