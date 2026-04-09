package com.ksetrasevakah.core.domain.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import kotlinx.coroutines.flow.Flow

interface TelemetryRepository {
    fun observeRecent(days: Int): Flow<Result<List<TelemetryEntity>>>
    suspend fun getRecentList(days: Int): Result<List<TelemetryEntity>>

    suspend fun getRecentSince(since: Long): Result<List<TelemetryEntity>>
    suspend fun getAvgVoltage(since: Long, until: Long): Result<Float?>
    suspend fun insert(entity: TelemetryEntity): Result<Long>
}
