package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.BackupLogEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BackupLogDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var dao: BackupLogDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.backupLogDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndGetRecentOrderedDesc() = runBlocking {
        dao.insert(BackupLogEntity(timestamp = 10L, type = "FULL", status = "OK"))
        dao.insert(BackupLogEntity(timestamp = 20L, type = "INCR", status = "OK"))
        val recent = dao.getRecent(10)
        assertEquals(2, recent.size)
        assertEquals(20L, recent[0].timestamp)
    }

    @Test
    fun getRecentRespectsLimit() = runBlocking {
        repeat(5) { i ->
            dao.insert(BackupLogEntity(timestamp = i.toLong(), type = "T", status = "OK"))
        }
        assertEquals(3, dao.getRecent(3).size)
    }

    @Test
    fun insertReturnsId() = runBlocking {
        val id = dao.insert(BackupLogEntity(timestamp = 1L, type = "T", status = "FAIL"))
        assertTrue(id > 0L)
    }

    @Test
    fun storesOptionalFields() = runBlocking {
        dao.insert(
            BackupLogEntity(
                timestamp = 1L,
                type = "T",
                status = "ERR",
                fileSizeBytes = 100L,
                errorMessage = "oops"
            )
        )
        val row = dao.getRecent(1).first()
        assertEquals("oops", row.errorMessage)
    }
}
