package com.ksetrasevakah.core.data.repository

import com.ksetrasevakah.core.data.preferences.AppPreferencesRepository
import com.ksetrasevakah.core.database.dao.BackupLogDao
import com.ksetrasevakah.core.database.dao.ChatMessageDao
import com.ksetrasevakah.core.database.dao.ChatThreadDao
import com.ksetrasevakah.core.database.dao.FaultDao
import com.ksetrasevakah.core.database.dao.SecurityEventDao
import com.ksetrasevakah.core.database.dao.TelemetryDao
import com.ksetrasevakah.core.database.dao.VectorDocumentDao
import com.ksetrasevakah.core.database.dao.WorkerActivityDao
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import com.ksetrasevakah.core.database.entity.VectorDocumentEntity
import com.ksetrasevakah.core.domain.repository.DataDiagnosticsRepository
import com.ksetrasevakah.core.domain.repository.AppDatabaseOverview
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataDiagnosticsRepositoryImpl @Inject constructor(
    private val telemetryDao: TelemetryDao,
    private val vectorDocumentDao: VectorDocumentDao,
    private val workerActivityDao: WorkerActivityDao,
    private val faultDao: FaultDao,
    private val chatThreadDao: ChatThreadDao,
    private val chatMessageDao: ChatMessageDao,
    private val securityEventDao: SecurityEventDao,
    private val backupLogDao: BackupLogDao,
    private val appPreferences: AppPreferencesRepository
) : DataDiagnosticsRepository {

    override suspend fun loadOverview(): AppDatabaseOverview {
        return AppDatabaseOverview(
            telemetryRowCount = telemetryDao.count(),
            telemetryOldestMs = telemetryDao.minTimestamp(),
            telemetryNewestMs = telemetryDao.maxTimestamp(),
            lastPipelineIngestWallClockMs = appPreferences.getLastTelemetryIngestAtOnce(),
            vectorChunkCount = vectorDocumentDao.count(),
            vectorOldestMs = vectorDocumentDao.minCreatedAt(),
            vectorNewestMs = vectorDocumentDao.maxCreatedAt(),
            workerActivityRowCount = workerActivityDao.count(),
            faultRowCount = faultDao.count(),
            chatThreadCount = chatThreadDao.count(),
            chatMessageCount = chatMessageDao.count(),
            securityEventCount = securityEventDao.count(),
            backupLogRowCount = backupLogDao.count(),
            modelsDownloaded = appPreferences.areModelsDownloadedOnce(),
            firstRunComplete = appPreferences.isFirstRunCompleteOnce()
        )
    }

    override suspend fun recentTelemetry(limit: Int): List<TelemetryEntity> =
        telemetryDao.getRecentPreview(limit.coerceIn(1, 200))

    override suspend fun recentVectorChunks(limit: Int): List<VectorDocumentEntity> =
        vectorDocumentDao.getRecent(limit.coerceIn(1, 200))
}
