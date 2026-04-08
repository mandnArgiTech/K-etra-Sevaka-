package com.ksetrasevakah.core.backup

import com.ksetrasevakah.core.common.Result
import java.io.File

interface DriveApiClient {
    suspend fun upload(file: File, folderName: String): Result<String>
    suspend fun download(fileId: String, destination: File): Result<File>
    suspend fun listBackups(folderName: String): Result<List<BackupFileInfo>>
    suspend fun delete(fileId: String): Result<Unit>
}

data class BackupFileInfo(
    val fileId: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val createdAt: Long
)
