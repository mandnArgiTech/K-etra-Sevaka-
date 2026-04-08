package com.ksetrasevakah.core.domain.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import kotlinx.coroutines.flow.Flow

interface WorkerActivityRepository {
    fun observeLast14Days(): Flow<Result<List<WorkerActivityEntity>>>
    suspend fun getAvgOnTime(days: Int): Result<Long?>
    suspend fun getForgotOffCount(sinceDays: Int): Result<Int>
    suspend fun upsert(entity: WorkerActivityEntity): Result<Unit>
    suspend fun getByDate(date: String): Result<WorkerActivityEntity?>
}
