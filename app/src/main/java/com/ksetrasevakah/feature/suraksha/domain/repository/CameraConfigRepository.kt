package com.ksetrasevakah.feature.suraksha.domain.repository

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.domain.model.CameraConfig
import com.ksetrasevakah.feature.suraksha.domain.model.CameraMode
import kotlinx.coroutines.flow.Flow

interface CameraConfigRepository {
    fun observeAllCameras(): Flow<Result<List<CameraConfig>>>
    suspend fun getByName(name: String): Result<CameraConfig?>
    suspend fun insert(config: CameraConfig): Result<Long>
    suspend fun updateMode(cameraName: String, mode: CameraMode): Result<Unit>
    suspend fun updateCameraMode(id: Long, mode: CameraMode): Result<Unit>
    suspend fun updateLastSeen(cameraName: String, timestamp: Long): Result<Unit>
}
