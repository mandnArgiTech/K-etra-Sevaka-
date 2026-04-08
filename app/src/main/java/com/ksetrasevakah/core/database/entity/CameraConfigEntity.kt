package com.ksetrasevakah.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "camera_config",
    indices = [Index(value = ["camera_name"], unique = true)]
)
data class CameraConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "camera_name") val cameraName: String,
    @ColumnInfo(name = "mode") val mode: String,
    @ColumnInfo(name = "last_seen") val lastSeen: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long
)
