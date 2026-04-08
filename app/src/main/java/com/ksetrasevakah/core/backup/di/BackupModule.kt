package com.ksetrasevakah.core.backup.di

import com.ksetrasevakah.core.backup.BackupFileInfo
import com.ksetrasevakah.core.backup.BackupManager
import com.ksetrasevakah.core.backup.DriveApiClient
import com.ksetrasevakah.core.backup.RestoreManager
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.dao.BackupLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackupModule {

    @Provides
    @Singleton
    fun provideDriveApiClient(): DriveApiClient = object : DriveApiClient {
        override suspend fun upload(file: File, folderName: String): Result<String> =
            Result.Error("Google Drive not configured")

        override suspend fun download(fileId: String, destination: File): Result<File> =
            Result.Error("Google Drive not configured")

        override suspend fun listBackups(folderName: String): Result<List<BackupFileInfo>> =
            Result.Success(emptyList())

        override suspend fun delete(fileId: String): Result<Unit> =
            Result.Error("Google Drive not configured")
    }

    @Provides
    @Singleton
    fun provideBackupManager(
        database: KsetraDatabase,
        backupLogDao: BackupLogDao,
        driveApiClient: DriveApiClient
    ): BackupManager = BackupManager(database, backupLogDao, driveApiClient)

    @Provides
    @Singleton
    fun provideRestoreManager(
        database: KsetraDatabase,
        backupLogDao: BackupLogDao,
        driveApiClient: DriveApiClient
    ): RestoreManager = RestoreManager(database, backupLogDao, driveApiClient)
}
