package com.ksetrasevakah.core.database.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.FaultEntity
import kotlinx.coroutines.flow.Flow

data class FaultCount(
    @ColumnInfo(name = "fault_type") val faultType: String,
    @ColumnInfo(name = "count") val count: Int
)

@Dao
interface FaultDao {
    @Query("SELECT fault_type, COUNT(*) as count FROM fault_log WHERE timestamp >= :since GROUP BY fault_type")
    suspend fun getFaultDistribution(since: Long): List<FaultCount>

    @Query("SELECT * FROM fault_log WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getRecent(since: Long): Flow<List<FaultEntity>>

    @Query("SELECT * FROM fault_log ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentList(limit: Int = 50): List<FaultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FaultEntity): Long
}
