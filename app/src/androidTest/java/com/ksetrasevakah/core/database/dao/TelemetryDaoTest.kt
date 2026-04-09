package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.TelemetryEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TelemetryDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var dao: TelemetryDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.telemetryDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndObserveRecent() = runBlocking {
        val t = 5000L
        dao.insert(TelemetryEntity(rawSms = "x", timestamp = t, motorOn = true, voltage = 220f))
        val list = dao.getRecent(0L).first()
        assertEquals(1, list.size)
        assertEquals(t, list[0].timestamp)
    }

    @Test
    fun getRecentListFiltersBySince() = runBlocking {
        dao.insert(TelemetryEntity(rawSms = "a", timestamp = 100L, motorOn = false))
        dao.insert(TelemetryEntity(rawSms = "b", timestamp = 200L, motorOn = true))
        val list = dao.getRecentList(150L)
        assertEquals(1, list.size)
        assertEquals(200L, list[0].timestamp)
    }

    @Test
    fun getAvgVoltage() = runBlocking {
        dao.insert(TelemetryEntity(rawSms = "a", timestamp = 10L, motorOn = false, voltage = 200f))
        dao.insert(TelemetryEntity(rawSms = "b", timestamp = 20L, motorOn = true, voltage = 220f))
        val avg = dao.getAvgVoltage(0L, 100L)
        assertEquals(210f, avg!!, 0.01f)
    }

    @Test
    fun insertReturnsId() = runBlocking {
        val id = dao.insert(TelemetryEntity(rawSms = "z", timestamp = 1L, motorOn = false))
        assertTrue(id > 0L)
    }
}
