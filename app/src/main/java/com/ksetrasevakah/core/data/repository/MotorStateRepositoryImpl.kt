package com.ksetrasevakah.core.data.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.MotorStateDao
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.designsystem.model.MotorState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MotorStateRepositoryImpl @Inject constructor(
    private val dao: MotorStateDao,
) : MotorStateRepository {

    override fun observeMotorState(): Flow<Result<MotorState>> =
        dao.observe().map { entity ->
            try {
                if (entity != null) {
                    Result.Success(MotorState.valueOf(entity.state))
                } else {
                    Result.Success(MotorState.OFF)
                }
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to observe motor state", e)
            }
        }

    override suspend fun updateMotorState(state: MotorState): Result<Unit> =
        try {
            dao.updateState(state.name)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update motor state", e)
        }

    override suspend fun setPendingCommand(command: String): Result<Unit> =
        try {
            dao.setPendingCommand(command, System.currentTimeMillis())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to set pending command", e)
        }

    override suspend fun clearPendingCommand(): Result<Unit> =
        try {
            dao.setPendingCommand(null, null)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to clear pending command", e)
        }

    override suspend fun getSessionDuration(): Result<Long> =
        try {
            val entity = dao.get()
            val start = entity?.currentSessionStart
            if (start != null) {
                Result.Success(System.currentTimeMillis() - start)
            } else {
                Result.Success(0L)
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get session duration", e)
        }
}
