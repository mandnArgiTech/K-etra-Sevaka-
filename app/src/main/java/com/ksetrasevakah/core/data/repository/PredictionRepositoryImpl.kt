package com.ksetrasevakah.core.data.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.PredictionCacheDao
import com.ksetrasevakah.core.database.entity.PredictionCacheEntity
import com.ksetrasevakah.core.domain.repository.PredictionRepository
import javax.inject.Inject

class PredictionRepositoryImpl @Inject constructor(
    private val dao: PredictionCacheDao,
) : PredictionRepository {

    override suspend fun getByType(type: String): Result<PredictionCacheEntity?> =
        try {
            Result.Success(dao.getByType(type))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get prediction by type", e)
        }

    override suspend fun upsert(entity: PredictionCacheEntity): Result<Unit> =
        try {
            dao.upsert(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to upsert prediction", e)
        }
}
