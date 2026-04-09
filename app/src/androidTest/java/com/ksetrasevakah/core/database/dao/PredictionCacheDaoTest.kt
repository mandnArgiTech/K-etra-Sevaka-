package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.PredictionCacheEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PredictionCacheDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var dao: PredictionCacheDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.predictionCacheDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun upsertAndGetByType() = runBlocking {
        val now = System.currentTimeMillis()
        dao.upsert(
            PredictionCacheEntity(
                type = "failure",
                resultJson = "{}",
                confidence = 0.5f,
                computedAt = now,
                validUntil = now + 1
            )
        )
        val row = dao.getByType("failure")
        assertEquals(0.5f, row!!.confidence, 0f)
    }

    @Test
    fun overwriteSameType() = runBlocking {
        val now = System.currentTimeMillis()
        dao.upsert(
            PredictionCacheEntity(type = "x", resultJson = "a", confidence = 0.1f, computedAt = now, validUntil = now)
        )
        dao.upsert(
            PredictionCacheEntity(type = "x", resultJson = "b", confidence = 0.9f, computedAt = now, validUntil = now)
        )
        assertEquals(0.9f, dao.getByType("x")!!.confidence, 0f)
    }

    @Test
    fun deleteByType() = runBlocking {
        val now = System.currentTimeMillis()
        dao.upsert(
            PredictionCacheEntity(type = "y", resultJson = "", confidence = 0f, computedAt = now, validUntil = now)
        )
        dao.deleteByType("y")
        assertNull(dao.getByType("y"))
    }

    @Test
    fun getByTypeMissingReturnsNull() = runBlocking {
        assertNull(dao.getByType("none"))
    }
}
