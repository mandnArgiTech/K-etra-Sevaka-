package com.ksetrasevakah.core.data.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.TelemetryDao
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.domain.repository.TelemetryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TelemetryRepositoryImpl @Inject constructor(
    private val dao: TelemetryDao,
) : TelemetryRepository {

    override fun observeRecent(days: Int): Flow<Result<List<TelemetryEntity>>> {
        val since = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L)
        return dao.getRecent(since).map { list ->
            try {
                Result.Success(list)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to observe telemetry", e)
            }
        }
    }

    override suspend fun getRecentList(days: Int): Result<List<TelemetryEntity>> =
        try {
            val since = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L)
            Result.Success(dao.getRecentList(since))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get telemetry list", e)
        }

    override suspend fun getRecentSince(since: Long): Result<List<TelemetryEntity>> =
        try {
            Result.Success(dao.getRecentList(since))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get telemetry since", e)
        }

    override suspend fun getAvgVoltage(since: Long, until: Long): Result<Float?> =
        try {
            Result.Success(dao.getAvgVoltage(since, until))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get average voltage", e)
        }

    override suspend fun insert(entity: TelemetryEntity): Result<Long> =
        try {
            Result.Success(dao.insert(entity))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to insert telemetry", e)
        }
}
