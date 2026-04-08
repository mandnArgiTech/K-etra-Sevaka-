package com.ksetrasevakah.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "fault_log", indices = [Index("timestamp")])
data class FaultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "fault_type") val faultType: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "auto_recovered") val autoRecovered: Boolean = false,
    @ColumnInfo(name = "recovery_time") val recoveryTime: Long? = null
)
