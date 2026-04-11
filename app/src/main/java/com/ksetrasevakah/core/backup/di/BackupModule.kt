package com.ksetrasevakah.core.backup.di

import com.ksetrasevakah.core.backup.BackupManager
import com.ksetrasevakah.core.backup.DriveApiClient
import com.ksetrasevakah.core.backup.GoogleDriveClient
import com.ksetrasevakah.core.backup.RestoreManager
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.dao.BackupLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BackupModule {

    @Provides
    @Singleton
    fun provideDriveApiClient(impl: GoogleDriveClient): DriveApiClient = impl

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
