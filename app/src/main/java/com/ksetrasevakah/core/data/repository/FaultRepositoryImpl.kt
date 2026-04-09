package com.ksetrasevakah.core.data.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.FaultCount
import com.ksetrasevakah.core.database.dao.FaultDao
import com.ksetrasevakah.core.database.entity.FaultEntity
import com.ksetrasevakah.core.domain.repository.FaultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FaultRepositoryImpl @Inject constructor(
    private val dao: FaultDao,
) : FaultRepository {

    override suspend fun getFaultDistribution(days: Int): Result<List<FaultCount>> =
        try {
            val since = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L)
            Result.Success(dao.getFaultDistribution(since))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get fault distribution", e)
        }

    override fun observeRecent(days: Int): Flow<Result<List<FaultEntity>>> {
        val since = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L)
        return dao.getRecent(since).map { list ->
            try {
                Result.Success(list)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to observe faults", e)
            }
        }
    }

    override suspend fun insert(entity: FaultEntity): Result<Long> =
        try {
            Result.Success(dao.insert(entity))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to insert fault", e)
        }

    override suspend fun getRecentSince(since: Long): Result<List<FaultEntity>> =
        try {
            Result.Success(dao.getRecentSince(since))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get faults since", e)
        }
}
