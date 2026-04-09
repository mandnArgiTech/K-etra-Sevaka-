package com.ksetrasevakah.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ksetrasevakah.core.database.entity.CameraConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CameraConfigDao {

    @Query("SELECT * FROM camera_config ORDER BY camera_name ASC")
    fun getAllCameras(): Flow<List<CameraConfigEntity>>

    @Query("SELECT * FROM camera_config WHERE camera_name = :name LIMIT 1")
    suspend fun getByName(name: String): CameraConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CameraConfigEntity): Long

    @Query("UPDATE camera_config SET mode = :mode WHERE camera_name = :cameraName")
    suspend fun updateMode(cameraName: String, mode: String)

    @Query("UPDATE camera_config SET mode = :mode WHERE id = :id")
    suspend fun updateModeById(id: Long, mode: String)

    @Query("UPDATE camera_config SET last_seen = :timestamp WHERE camera_name = :cameraName")
    suspend fun updateLastSeen(cameraName: String, timestamp: Long)
}
