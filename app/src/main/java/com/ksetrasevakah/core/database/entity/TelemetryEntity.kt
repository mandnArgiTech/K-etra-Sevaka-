package com.ksetrasevakah.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "telemetry_log", indices = [Index("timestamp")])
data class TelemetryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "raw_sms") val rawSms: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "motor_on") val motorOn: Boolean,
    @ColumnInfo(name = "phase_r") val phaseR: Float? = null,
    @ColumnInfo(name = "phase_y") val phaseY: Float? = null,
    @ColumnInfo(name = "phase_b") val phaseB: Float? = null,
    @ColumnInfo(name = "voltage") val voltage: Float? = null,
    @ColumnInfo(name = "temperature") val temperature: Float? = null,
    @ColumnInfo(name = "runtime_minutes") val runtimeMinutes: Int? = null,
    @ColumnInfo(name = "narrative") val narrative: String? = null
)
