package com.ksetrasevakah.core.backup.model

sealed class BackupStatus {
    data object Idle : BackupStatus()
    data class InProgress(val progress: Float = 0f) : BackupStatus()
    data class Success(val timestamp: Long, val fileSizeBytes: Long) : BackupStatus()
    data class Error(val message: String, val throwable: Throwable? = null) : BackupStatus()
}
