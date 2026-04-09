package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.FaultEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FaultDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var dao: FaultDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.faultDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndGetRecentSince() = runBlocking {
        dao.insert(FaultEntity(timestamp = 300L, faultType = "DRY", description = "d"))
        val list = dao.getRecentSince(0L)
        assertEquals(1, list.size)
        assertEquals("DRY", list[0].faultType)
    }

    @Test
    fun getFaultDistributionGroupsByType() = runBlocking {
        dao.insert(FaultEntity(timestamp = 1L, faultType = "A", description = "1"))
        dao.insert(FaultEntity(timestamp = 2L, faultType = "A", description = "2"))
        dao.insert(FaultEntity(timestamp = 3L, faultType = "B", description = "3"))
        val dist = dao.getFaultDistribution(0L)
        assertEquals(2, dist.size)
        val a = dist.find { it.faultType == "A" }
        assertEquals(2, a?.count)
    }

    @Test
    fun observeRecentFlow() = runBlocking {
        dao.insert(FaultEntity(timestamp = 10L, faultType = "X", description = ""))
        val list = dao.getRecent(0L).first()
        assertEquals(1, list.size)
    }

    @Test
    fun getRecentListLimit() = runBlocking {
        repeat(3) { i ->
            dao.insert(FaultEntity(timestamp = i.toLong(), faultType = "T", description = ""))
        }
        val list = dao.getRecentList(2)
        assertEquals(2, list.size)
    }
}
