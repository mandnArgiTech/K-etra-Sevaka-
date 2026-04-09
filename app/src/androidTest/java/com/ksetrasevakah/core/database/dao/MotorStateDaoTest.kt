package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.MotorStateEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MotorStateDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var dao: MotorStateDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.motorStateDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun upsertAndGet() = runBlocking {
        dao.upsert(MotorStateEntity(state = "ON"))
        val row = dao.get()
        assertNotNull(row)
        assertEquals("ON", row!!.state)
    }

    @Test
    fun observeEmitsUpsertedState() = runBlocking {
        dao.upsert(MotorStateEntity(state = "OFF"))
        val emitted = dao.observe().first()
        assertEquals("OFF", emitted?.state)
    }

    @Test
    fun updateState() = runBlocking {
        dao.upsert(MotorStateEntity(state = "ON"))
        dao.updateState("OFF", now = 99L)
        assertEquals("OFF", dao.get()?.state)
    }

    @Test
    fun setPendingCommand() = runBlocking {
        dao.upsert(MotorStateEntity(state = "OFF"))
        dao.setPendingCommand("START", 10L, now = 20L)
        val row = dao.get()
        assertEquals("START", row?.pendingCommand)
        assertEquals(10L, row?.pendingSince)
    }
}
