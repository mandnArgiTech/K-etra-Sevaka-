package com.ksetrasevakah.feature.suraksha.data.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.CameraConfigDao
import com.ksetrasevakah.core.database.entity.CameraConfigEntity
import com.ksetrasevakah.feature.suraksha.domain.model.CameraConfig
import com.ksetrasevakah.feature.suraksha.domain.model.CameraMode
import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CameraConfigRepositoryImpl @Inject constructor(
    private val dao: CameraConfigDao
) : CameraConfigRepository {

    override fun observeAllCameras(): Flow<Result<List<CameraConfig>>> =
        dao.getAllCameras().map { entities ->
            try {
                Result.Success(entities.map { it.toDomain() })
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to observe cameras", e)
            }
        }

    override suspend fun getByName(name: String): Result<CameraConfig?> =
        try {
            Result.Success(dao.getByName(name)?.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to get camera config", e)
        }

    override suspend fun insert(config: CameraConfig): Result<Long> =
        try {
            Result.Success(dao.insert(config.toEntity()))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to insert camera config", e)
        }

    override suspend fun updateMode(cameraName: String, mode: CameraMode): Result<Unit> =
        try {
            dao.updateMode(cameraName, mode.name)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update camera mode", e)
        }

    override suspend fun updateCameraMode(id: Long, mode: CameraMode): Result<Unit> {
        if (id <= 0L) {
            return Result.Error("Invalid camera id")
        }
        return try {
            dao.updateModeById(id, mode.name)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update camera mode", e)
        }
    }

    override suspend fun updateLastSeen(cameraName: String, timestamp: Long): Result<Unit> =
        try {
            dao.updateLastSeen(cameraName, timestamp)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update last seen", e)
        }

    private fun CameraConfigEntity.toDomain() = CameraConfig(
        id = id,
        cameraName = cameraName,
        mode = CameraMode.fromString(mode),
        lastSeen = lastSeen,
        createdAt = createdAt
    )

    private fun CameraConfig.toEntity() = CameraConfigEntity(
        id = id,
        cameraName = cameraName,
        mode = mode.name,
        lastSeen = lastSeen,
        createdAt = createdAt
    )
}
