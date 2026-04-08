package com.ksetrasevakah.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_briefings")
data class SecurityBriefingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "generated_at") val generatedAt: Long,
    @ColumnInfo(name = "period_start") val periodStart: Long,
    @ColumnInfo(name = "period_end") val periodEnd: Long,
    @ColumnInfo(name = "summary") val summary: String,
    @ColumnInfo(name = "total_events") val totalEvents: Int,
    @ColumnInfo(name = "critical_count") val criticalCount: Int,
    @ColumnInfo(name = "high_count") val highCount: Int
)
