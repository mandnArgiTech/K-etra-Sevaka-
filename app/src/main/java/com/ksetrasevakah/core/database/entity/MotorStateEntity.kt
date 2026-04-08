package com.ksetrasevakah.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "motor_state")
data class MotorStateEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "state") val state: String,
    @ColumnInfo(name = "last_on_time") val lastOnTime: Long? = null,
    @ColumnInfo(name = "last_off_time") val lastOffTime: Long? = null,
    @ColumnInfo(name = "current_session_start") val currentSessionStart: Long? = null,
    @ColumnInfo(name = "pending_command") val pendingCommand: String? = null,
    @ColumnInfo(name = "pending_since") val pendingSince: Long? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
