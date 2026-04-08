package com.ksetrasevakah.core.domain.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.FaultCount
import com.ksetrasevakah.core.database.entity.FaultEntity
import kotlinx.coroutines.flow.Flow

interface FaultRepository {
    suspend fun getFaultDistribution(days: Int): Result<List<FaultCount>>
    fun observeRecent(days: Int): Flow<Result<List<FaultEntity>>>
    suspend fun insert(entity: FaultEntity): Result<Long>
}
