package com.ksetrasevakah.core.database.model

import androidx.room.ColumnInfo

data class ThreatCount(
    @ColumnInfo(name = "threat_level") val threatLevel: String,
    @ColumnInfo(name = "count") val count: Int
)
