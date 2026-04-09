package com.ksetrasevakah.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Version 3: security_events.acknowledged is INTEGER 0/1 explicitly (was Boolean in Kotlin).
 * SQLite column type unchanged; no ALTER needed.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // No-op: acknowledged remains INTEGER 0/1
    }
}
