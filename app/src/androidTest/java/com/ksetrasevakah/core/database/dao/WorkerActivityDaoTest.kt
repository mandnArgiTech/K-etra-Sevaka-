package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.WorkerActivityEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkerActivityDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var dao: WorkerActivityDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.workerActivityDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun upsertAndGetByDate() = runBlocking {
        dao.upsert(WorkerActivityEntity(date = "2025-01-01", onTime = 100L))
        val row = dao.getByDate("2025-01-01")
        assertNotNull(row)
        assertEquals(100L, row!!.onTime)
    }

    @Test
    fun getLast14DaysFlow() = runBlocking {
        dao.upsert(WorkerActivityEntity(date = "2025-01-02", onTime = 1L))
        val list = dao.getLast14Days().first()
        assertEquals(1, list.size)
    }

    @Test
    fun getLastNDays() = runBlocking {
        dao.upsert(WorkerActivityEntity(date = "2025-01-03", onTime = 1L))
        dao.upsert(WorkerActivityEntity(date = "2025-01-04", onTime = 2L))
        val list = dao.getLastNDays(10)
        assertEquals(2, list.size)
    }

    @Test
    fun getAvgOnTime() = runBlocking {
        dao.upsert(WorkerActivityEntity(date = "2025-01-05", onTime = 100L))
        dao.upsert(WorkerActivityEntity(date = "2025-01-06", onTime = 200L))
        val avg = dao.getAvgOnTime(14)
        assertNotNull(avg)
    }
}
