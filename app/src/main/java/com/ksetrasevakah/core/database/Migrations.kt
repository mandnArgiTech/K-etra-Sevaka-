package com.ksetrasevakah.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adds Surakṣā tables present in DB v2 (see exported [2.json]).
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `security_events` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `camera_name` TEXT NOT NULL,
                `event_type` TEXT NOT NULL,
                `threat_level` TEXT NOT NULL,
                `confidence` REAL NOT NULL,
                `origin_timestamp` INTEGER NOT NULL,
                `received_timestamp` INTEGER NOT NULL,
                `hour_of_day` INTEGER NOT NULL,
                `summary` TEXT,
                `acknowledged` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_security_events_camera_name` ON `security_events` (`camera_name`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_security_events_origin_timestamp` ON `security_events` (`origin_timestamp`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_security_events_threat_level` ON `security_events` (`threat_level`)"
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_security_events_hour_of_day` ON `security_events` (`hour_of_day`)"
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `camera_config` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `camera_name` TEXT NOT NULL,
                `mode` TEXT NOT NULL,
                `last_seen` INTEGER NOT NULL,
                `created_at` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS `index_camera_config_camera_name` ON `camera_config` (`camera_name`)"
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `security_briefings` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `generated_at` INTEGER NOT NULL,
                `period_start` INTEGER NOT NULL,
                `period_end` INTEGER NOT NULL,
                `summary` TEXT NOT NULL,
                `total_events` INTEGER NOT NULL,
                `critical_count` INTEGER NOT NULL,
                `high_count` INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

/**
 * Version 3: security_events.acknowledged is INTEGER 0/1 explicitly (was Boolean in Kotlin).
 * SQLite column type unchanged; no ALTER needed.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // No-op: acknowledged remains INTEGER 0/1
    }
}

/**
 * RAG vector document persistence.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `vector_documents` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `text` TEXT NOT NULL,
                `embedding` BLOB NOT NULL,
                `metadataJson` TEXT NOT NULL,
                `createdAt` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_vector_documents_createdAt` ON `vector_documents` (`createdAt`)"
        )
    }
}
