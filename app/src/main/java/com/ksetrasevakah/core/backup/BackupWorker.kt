package com.ksetrasevakah.core.backup

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ksetrasevakah.core.common.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val backupManager: BackupManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val backupDir = File(applicationContext.filesDir, "backups")

        return when (val result = backupManager.createBackup(backupDir)) {
            is com.ksetrasevakah.core.common.Result.Success -> {
                val uploadResult = backupManager.uploadBackup(result.data)
                if (uploadResult is com.ksetrasevakah.core.common.Result.Success) {
                    Result.success()
                } else {
                    Result.retry()
                }
            }
            is com.ksetrasevakah.core.common.Result.Error -> Result.retry()
            is com.ksetrasevakah.core.common.Result.Loading -> Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "ksetra_scheduled_backup"
    }
}
