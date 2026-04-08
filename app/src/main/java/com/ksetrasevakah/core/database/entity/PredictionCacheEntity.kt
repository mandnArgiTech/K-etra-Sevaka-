package com.ksetrasevakah.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_cache")
data class PredictionCacheEntity(
    @PrimaryKey val type: String,
    @ColumnInfo(name = "result_json") val resultJson: String,
    @ColumnInfo(name = "confidence") val confidence: Float,
    @ColumnInfo(name = "computed_at") val computedAt: Long,
    @ColumnInfo(name = "valid_until") val validUntil: Long
)
