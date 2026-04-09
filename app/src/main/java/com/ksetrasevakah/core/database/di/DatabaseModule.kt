package com.ksetrasevakah.core.database.di

import android.content.Context
import androidx.room.Room
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.MIGRATION_2_3
import com.ksetrasevakah.core.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KsetraDatabase =
        Room.databaseBuilder(context, KsetraDatabase::class.java, Constants.DB_NAME)
            .addMigrations(MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideMotorStateDao(db: KsetraDatabase): MotorStateDao = db.motorStateDao()
    @Provides fun provideTelemetryDao(db: KsetraDatabase): TelemetryDao = db.telemetryDao()
    @Provides fun provideFaultDao(db: KsetraDatabase): FaultDao = db.faultDao()
    @Provides fun provideWorkerActivityDao(db: KsetraDatabase): WorkerActivityDao = db.workerActivityDao()
    @Provides fun providePredictionCacheDao(db: KsetraDatabase): PredictionCacheDao = db.predictionCacheDao()
    @Provides fun provideChatThreadDao(db: KsetraDatabase): ChatThreadDao = db.chatThreadDao()
    @Provides fun provideChatMessageDao(db: KsetraDatabase): ChatMessageDao = db.chatMessageDao()
    @Provides fun provideBackupLogDao(db: KsetraDatabase): BackupLogDao = db.backupLogDao()
    @Provides fun provideSecurityEventDao(db: KsetraDatabase): SecurityEventDao = db.securityEventDao()
    @Provides fun provideCameraConfigDao(db: KsetraDatabase): CameraConfigDao = db.cameraConfigDao()
    @Provides fun provideSecurityBriefingDao(db: KsetraDatabase): SecurityBriefingDao = db.securityBriefingDao()
}
