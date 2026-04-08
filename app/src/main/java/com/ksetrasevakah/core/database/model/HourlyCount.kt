package com.ksetrasevakah.core.database.model

import androidx.room.ColumnInfo

data class HourlyCount(
    @ColumnInfo(name = "hour_of_day") val hourOfDay: Int,
    @ColumnInfo(name = "count") val count: Int
)
