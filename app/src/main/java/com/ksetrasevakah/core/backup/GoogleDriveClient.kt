package com.ksetrasevakah.core.backup

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File as DriveFile
import com.google.api.services.drive.model.FileList
import com.ksetrasevakah.core.common.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleDriveClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signInManager: GoogleSignInManager
) : DriveApiClient {

    private fun buildDrive(credential: GoogleAccountCredential): Drive =
        Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName(context.getString(com.ksetrasevakah.R.string.app_name))
            .build()

    override suspend fun upload(file: File, folderName: String): Result<String> = withContext(Dispatchers.IO) {
        val cred = signInManager.credentialOrNull()
            ?: return@withContext Result.Error("Sign in with Google in Settings to enable Drive backup")
        try {
            val drive = buildDrive(cred)
            val metadata = DriveFile().apply {
                name = file.name
                parents = listOf("appDataFolder")
            }
            val media = FileContent("application/octet-stream", file)
            val created = drive.files().create(metadata, media).setFields("id").execute()
            Result.Success(created.id)
        } catch (e: Exception) {
            Result.Error("Drive upload failed: ${e.message}", e)
        }
    }

    override suspend fun download(fileId: String, destination: File): Result<File> = withContext(Dispatchers.IO) {
        val cred = signInManager.credentialOrNull()
            ?: return@withContext Result.Error("Sign in with Google in Settings")
        try {
            val drive = buildDrive(cred)
            drive.files().get(fileId).executeMediaAsInputStream().use { input ->
                FileOutputStream(destination).use { out -> input.copyTo(out) }
            }
            Result.Success(destination)
        } catch (e: Exception) {
            Result.Error("Drive download failed: ${e.message}", e)
        }
    }

    override suspend fun listBackups(folderName: String): Result<List<BackupFileInfo>> =
        withContext(Dispatchers.IO) {
            val cred = signInManager.credentialOrNull()
                ?: return@withContext Result.Error("Sign in with Google in Settings")
            try {
                val drive = buildDrive(cred)
                val result: FileList = drive.files().list()
                    .setSpaces("appDataFolder")
                    .setFields("files(id,name,size,createdTime)")
                    .execute()
                val list = result.files?.mapNotNull { f ->
                    val id = f.id ?: return@mapNotNull null
                    val name = f.name ?: "backup"
                    val size = (f.size as? Number)?.toLong() ?: 0L
                    val created = (f.createdTime?.value as? Number)?.toLong() ?: 0L
                    BackupFileInfo(
                        fileId = id,
                        fileName = name,
                        fileSizeBytes = size,
                        createdAt = created
                    )
                } ?: emptyList()
                Result.Success(list)
            } catch (e: Exception) {
                Result.Error("Drive list failed: ${e.message}", e)
            }
        }

    override suspend fun delete(fileId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val cred = signInManager.credentialOrNull()
            ?: return@withContext Result.Error("Sign in with Google in Settings")
        try {
            val drive = buildDrive(cred)
            drive.files().delete(fileId).execute()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Drive delete failed: ${e.message}", e)
        }
    }
}
