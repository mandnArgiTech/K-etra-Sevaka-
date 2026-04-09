package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.CameraConfigEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraConfigDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var dao: CameraConfigDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.cameraConfigDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndGetByName() = runBlocking {
        dao.insert(
            CameraConfigEntity(cameraName = "Zebra", mode = "ACTIVE", lastSeen = 1L, createdAt = 1L)
        )
        val row = dao.getByName("Zebra")
        assertEquals("ACTIVE", row?.mode)
    }

    @Test
    fun getAllCamerasSortedByName() = runBlocking {
        dao.insert(CameraConfigEntity(cameraName = "B", mode = "ACTIVE", lastSeen = 1L, createdAt = 1L))
        dao.insert(CameraConfigEntity(cameraName = "A", mode = "SILENT", lastSeen = 2L, createdAt = 2L))
        val list = dao.getAllCameras().first()
        assertEquals("A", list[0].cameraName)
        assertEquals("B", list[1].cameraName)
    }

    @Test
    fun updateMode() = runBlocking {
        dao.insert(CameraConfigEntity(cameraName = "C", mode = "ACTIVE", lastSeen = 1L, createdAt = 1L))
        dao.updateMode("C", "DROP")
        assertEquals("DROP", dao.getByName("C")?.mode)
    }

    @Test
    fun updateLastSeen() = runBlocking {
        dao.insert(CameraConfigEntity(cameraName = "D", mode = "ACTIVE", lastSeen = 1L, createdAt = 1L))
        dao.updateLastSeen("D", 999L)
        assertEquals(999L, dao.getByName("D")?.lastSeen)
    }
}
