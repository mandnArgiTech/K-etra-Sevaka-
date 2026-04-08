package com.ksetrasevakah.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "security_events",
    indices = [
        Index("camera_name"),
        Index("origin_timestamp"),
        Index("threat_level"),
        Index("hour_of_day")
    ]
)
data class SecurityEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "camera_name") val cameraName: String,
    @ColumnInfo(name = "event_type") val eventType: String,
    @ColumnInfo(name = "threat_level") val threatLevel: String,
    @ColumnInfo(name = "confidence") val confidence: Float,
    @ColumnInfo(name = "origin_timestamp") val originTimestamp: Long,
    @ColumnInfo(name = "received_timestamp") val receivedTimestamp: Long,
    @ColumnInfo(name = "hour_of_day") val hourOfDay: Int,
    @ColumnInfo(name = "summary") val summary: String? = null,
    @ColumnInfo(name = "acknowledged") val acknowledged: Boolean = false
)
