package com.ksetrasevakah.core.backup

import com.ksetrasevakah.core.backup.model.BackupStatus
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.dao.BackupLogDao
import com.ksetrasevakah.core.database.entity.BackupLogEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

class BackupManager @Inject constructor(
    private val database: KsetraDatabase,
    private val backupLogDao: BackupLogDao,
    private val driveApiClient: DriveApiClient
) {

    private val _status = MutableStateFlow<BackupStatus>(BackupStatus.Idle)
    val status: StateFlow<BackupStatus> = _status.asStateFlow()

    suspend fun createBackup(backupDir: File): Result<File> {
        _status.value = BackupStatus.InProgress()
        return try {
            val dbFile = database.openHelper.writableDatabase.path?.let { File(it) }
                ?: return Result.Error("Database path unavailable").also {
                    _status.value = BackupStatus.Idle
                }

            if (!backupDir.exists()) backupDir.mkdirs()
            val timestamp = System.currentTimeMillis()
            val backupFile = File(backupDir, "ksetra_backup_$timestamp.db")
            dbFile.copyTo(backupFile, overwrite = true)

            _status.value = BackupStatus.Success(timestamp, backupFile.length())
            backupLogDao.insert(
                BackupLogEntity(
                    type = "local",
                    status = "success",
                    fileSizeBytes = backupFile.length()
                )
            )
            Result.Success(backupFile)
        } catch (e: Exception) {
            _status.value = BackupStatus.Error(e.message ?: "Backup failed", e)
            backupLogDao.insert(
                BackupLogEntity(type = "local", status = "error", errorMessage = e.message)
            )
            Result.Error("Backup creation failed: ${e.message}", e)
        }
    }

    suspend fun uploadBackup(localFile: File): Result<String> {
        _status.value = BackupStatus.InProgress(0.5f)
        return try {
            val result = driveApiClient.upload(localFile, Constants.BACKUP_FOLDER_NAME)
            when (result) {
                is Result.Success -> {
                    _status.value = BackupStatus.Success(System.currentTimeMillis(), localFile.length())
                    backupLogDao.insert(
                        BackupLogEntity(
                            type = "drive",
                            status = "success",
                            fileSizeBytes = localFile.length()
                        )
                    )
                    result
                }
                is Result.Error -> {
                    _status.value = BackupStatus.Error(result.message)
                    backupLogDao.insert(
                        BackupLogEntity(type = "drive", status = "error", errorMessage = result.message)
                    )
                    result
                }
                is Result.Loading -> result
            }
        } catch (e: Exception) {
            _status.value = BackupStatus.Error(e.message ?: "Upload failed", e)
            Result.Error("Backup upload failed: ${e.message}", e)
        }
    }

    fun resetStatus() {
        _status.value = BackupStatus.Idle
    }
}
