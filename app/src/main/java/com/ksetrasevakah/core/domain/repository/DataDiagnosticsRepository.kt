package com.ksetrasevakah.core.domain.repository

import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.database.entity.VectorDocumentEntity

interface DataDiagnosticsRepository {
    suspend fun loadOverview(): AppDatabaseOverview
    suspend fun recentTelemetry(limit: Int): List<TelemetryEntity>
    suspend fun recentVectorChunks(limit: Int): List<VectorDocumentEntity>
}
