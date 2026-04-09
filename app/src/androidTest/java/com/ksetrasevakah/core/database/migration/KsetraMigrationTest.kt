package com.ksetrasevakah.core.database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.MIGRATION_1_2
import com.ksetrasevakah.core.database.MIGRATION_2_3
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KsetraMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        KsetraDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To3_preservesPumpIqData_andCreatesSurakshaTables() {
        val dbName = "ksetra_migration_test.db"
        helper.createDatabase(dbName, 1).apply {
            execSQL(
                """
                INSERT INTO motor_state (`id`, `state`, `last_on_time`, `last_off_time`, `current_session_start`, `pending_command`, `pending_since`, `updated_at`)
                VALUES (1, 'OFF', NULL, NULL, NULL, NULL, NULL, 4242)
                """.trimIndent()
            )
            close()
        }

        val db = helper.runMigrationsAndValidate(
            dbName,
            3,
            false,
            MIGRATION_1_2,
            MIGRATION_2_3
        )

        db.query("SELECT state, updated_at FROM motor_state WHERE id = 1").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("OFF", c.getString(0))
            assertEquals(4242L, c.getLong(1))
        }
        db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='security_events'").use { c ->
            assertTrue(c.moveToFirst())
            assertEquals("security_events", c.getString(0))
        }
        db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='camera_config'").use { c ->
            assertTrue(c.moveToFirst())
        }
        db.query("SELECT name FROM sqlite_master WHERE type='table' AND name='security_briefings'").use { c ->
            assertTrue(c.moveToFirst())
        }
        db.close()
    }
}
