package com.ksetrasevakah.core.domain.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.designsystem.model.MotorState
import kotlinx.coroutines.flow.Flow

interface MotorStateRepository {
    fun observeMotorState(): Flow<Result<MotorState>>
    suspend fun updateMotorState(state: MotorState): Result<Unit>
    suspend fun setPendingCommand(command: String): Result<Unit>
    suspend fun clearPendingCommand(): Result<Unit>
    suspend fun getSessionDuration(): Result<Long>
}
