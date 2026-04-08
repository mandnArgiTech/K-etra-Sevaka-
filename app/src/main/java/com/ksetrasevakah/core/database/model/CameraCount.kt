package com.ksetrasevakah.core.database.model

import androidx.room.ColumnInfo

data class CameraCount(
    @ColumnInfo(name = "camera_name") val cameraName: String,
    @ColumnInfo(name = "count") val count: Int
)
