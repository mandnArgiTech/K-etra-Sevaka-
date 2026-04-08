package com.ksetrasevakah.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "worker_activity", indices = [Index("date", unique = true)])
data class WorkerActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "on_time") val onTime: Long? = null,
    @ColumnInfo(name = "off_time") val offTime: Long? = null,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int? = null,
    @ColumnInfo(name = "forgot_off") val forgotOff: Boolean = false
)
