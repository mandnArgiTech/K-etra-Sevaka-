package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.SecurityBriefingEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityBriefingDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var dao: SecurityBriefingDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.securityBriefingDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndGetRecent() = runBlocking {
        val now = System.currentTimeMillis()
        dao.insert(
            SecurityBriefingEntity(
                generatedAt = now,
                periodStart = now - 1,
                periodEnd = now,
                summary = "Quiet night",
                totalEvents = 0,
                criticalCount = 0,
                highCount = 0
            )
        )
        val list = dao.getRecent(5)
        assertEquals(1, list.size)
        assertEquals("Quiet night", list[0].summary)
    }

    @Test
    fun getRecentReturnsNewestFirst() = runBlocking {
        dao.insert(
            SecurityBriefingEntity(
                generatedAt = 1L,
                periodStart = 0L,
                periodEnd = 1L,
                summary = "old",
                totalEvents = 0,
                criticalCount = 0,
                highCount = 0
            )
        )
        dao.insert(
            SecurityBriefingEntity(
                generatedAt = 2L,
                periodStart = 0L,
                periodEnd = 2L,
                summary = "new",
                totalEvents = 0,
                criticalCount = 0,
                highCount = 0
            )
        )
        assertEquals("new", dao.getRecent(1).first().summary)
    }

    @Test
    fun insertReturnsId() = runBlocking {
        val id = dao.insert(
            SecurityBriefingEntity(
                generatedAt = 10L,
                periodStart = 0L,
                periodEnd = 10L,
                summary = "s",
                totalEvents = 1,
                criticalCount = 0,
                highCount = 1
            )
        )
        assertTrue(id > 0L)
    }

    @Test
    fun multipleBriefingsGetRecentLimit() = runBlocking {
        repeat(4) { i ->
            dao.insert(
                SecurityBriefingEntity(
                    generatedAt = i.toLong(),
                    periodStart = 0L,
                    periodEnd = i.toLong(),
                    summary = "b$i",
                    totalEvents = 0,
                    criticalCount = 0,
                    highCount = 0
                )
            )
        }
        assertEquals(2, dao.getRecent(2).size)
    }
}
