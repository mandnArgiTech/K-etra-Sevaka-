package com.ksetrasevakah.core.data.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.WorkerActivityDao
import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import com.ksetrasevakah.core.domain.repository.WorkerActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class WorkerActivityRepositoryImpl @Inject constructor(
    private val dao: WorkerActivityDao,
) : WorkerActivityRepository {

    override fun observeLast14Days(): Flow<Result<List<WorkerActivityEntity>>> =
        dao.getLast14Days().map { list ->
            try {
                Result.Success(list)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to observe worker activity", e)
            }
        }

    override suspend fun getAvgOnTime(days: Int): Result<Long?> =
        try {
            Result.Success(dao.getAvgOnTime(days))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get average on time", e)
        }

    override suspend fun getForgotOffCount(sinceDays: Int): Result<Int> =
        try {
            val sinceDate = LocalDate.now().minusDays(sinceDays.toLong()).toString()
            Result.Success(dao.getForgotOffCount(sinceDate))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get forgot-off count", e)
        }

    override suspend fun upsert(entity: WorkerActivityEntity): Result<Unit> =
        try {
            dao.upsert(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to upsert worker activity", e)
        }

    override suspend fun getByDate(date: String): Result<WorkerActivityEntity?> =
        try {
            Result.Success(dao.getByDate(date))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get worker activity by date", e)
        }
}
