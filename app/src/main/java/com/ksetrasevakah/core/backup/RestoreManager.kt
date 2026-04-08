package com.ksetrasevakah.core.backup

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.dao.BackupLogDao
import com.ksetrasevakah.core.database.entity.BackupLogEntity
import java.io.File
import javax.inject.Inject

class RestoreManager @Inject constructor(
    private val database: KsetraDatabase,
    private val backupLogDao: BackupLogDao,
    private val driveApiClient: DriveApiClient
) {

    suspend fun restoreFromLocal(backupFile: File): Result<Unit> {
        return try {
            val dbPath = database.openHelper.writableDatabase.path
                ?: return Result.Error("Database path unavailable")

            database.close()
            backupFile.copyTo(File(dbPath), overwrite = true)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Restore failed: ${e.message}", e)
        }
    }

    suspend fun restoreFromDrive(fileId: String, tempDir: File): Result<Unit> {
        return try {
            if (!tempDir.exists()) tempDir.mkdirs()
            val tempFile = File(tempDir, "restore_temp.db")

            when (val downloadResult = driveApiClient.download(fileId, tempFile)) {
                is Result.Success -> restoreFromLocal(downloadResult.data)
                is Result.Error -> Result.Error(downloadResult.message, downloadResult.throwable)
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error("Drive restore failed: ${e.message}", e)
        }
    }

    suspend fun listAvailableBackups(): Result<List<BackupFileInfo>> {
        return driveApiClient.listBackups(Constants.BACKUP_FOLDER_NAME)
    }
}
