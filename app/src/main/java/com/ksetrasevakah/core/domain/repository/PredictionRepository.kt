package com.ksetrasevakah.core.domain.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.PredictionCacheEntity

interface PredictionRepository {
    suspend fun getByType(type: String): Result<PredictionCacheEntity?>
    suspend fun upsert(entity: PredictionCacheEntity): Result<Unit>
}
