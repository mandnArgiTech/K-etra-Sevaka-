package com.ksetrasevakah.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ksetrasevakah.core.database.converter.Converters
import com.ksetrasevakah.core.database.dao.*
import com.ksetrasevakah.core.database.entity.*

@Database(
    entities = [
        MotorStateEntity::class,
        TelemetryEntity::class,
        FaultEntity::class,
        WorkerActivityEntity::class,
        PredictionCacheEntity::class,
        ChatThreadEntity::class,
        ChatMessageEntity::class,
        BackupLogEntity::class,
        SecurityEventEntity::class,
        CameraConfigEntity::class,
        SecurityBriefingEntity::class,
        VectorDocumentEntity::class,
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class KsetraDatabase : RoomDatabase() {
    abstract fun motorStateDao(): MotorStateDao
    abstract fun telemetryDao(): TelemetryDao
    abstract fun faultDao(): FaultDao
    abstract fun workerActivityDao(): WorkerActivityDao
    abstract fun predictionCacheDao(): PredictionCacheDao
    abstract fun chatThreadDao(): ChatThreadDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun backupLogDao(): BackupLogDao
    abstract fun securityEventDao(): SecurityEventDao
    abstract fun cameraConfigDao(): CameraConfigDao
    abstract fun securityBriefingDao(): SecurityBriefingDao
    abstract fun vectorDocumentDao(): VectorDocumentDao
}
