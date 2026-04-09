package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.SecurityEventEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurityEventDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var dao: SecurityEventDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.securityEventDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    private fun baseEvent(
        camera: String = "cam",
        threat: String = "LOW",
        origin: Long = 1000L,
        hour: Int = 10,
        ack: Int = 0
    ) = SecurityEventEntity(
        cameraName = camera,
        eventType = "PERSON",
        threatLevel = threat,
        confidence = 0.5f,
        originTimestamp = origin,
        receivedTimestamp = origin,
        hourOfDay = hour,
        summary = "s",
        acknowledged = ack
    )

    @Test
    fun insertAndGetRecentEventsFlow() = runBlocking {
        dao.insert(baseEvent(origin = 5000L))
        val list = dao.getRecentEvents(0L, 10).first()
        assertEquals(1, list.size)
    }

    @Test
    fun getThreatDistributionGroupsByLevel() = runBlocking {
        dao.insert(baseEvent(threat = "HIGH"))
        dao.insert(baseEvent(threat = "HIGH", origin = 2000L))
        dao.insert(baseEvent(threat = "LOW", origin = 3000L))
        val dist = dao.getThreatDistribution(0L)
        assertEquals(2, dist.size)
        val highs = dist.find { it.threatLevel == "HIGH" }
        assertEquals(2, highs?.count)
    }

    @Test
    fun getUnacknowledgedHighCountFlow() = runBlocking {
        dao.insert(baseEvent(threat = "HIGH", ack = 0))
        dao.insert(baseEvent(threat = "CRITICAL", origin = 2L, ack = 0))
        dao.insert(baseEvent(threat = "LOW", origin = 3L, ack = 0))
        assertEquals(2, dao.getUnacknowledgedHighCount().first())
    }

    @Test
    fun acknowledgeUpdatesCount() = runBlocking {
        val id = dao.insert(baseEvent(threat = "HIGH"))
        assertEquals(1, dao.getUnacknowledgedHighCount().first())
        dao.acknowledge(id)
        assertEquals(0, dao.getUnacknowledgedHighCount().first())
    }

    @Test
    fun getHourlyHeatmap() = runBlocking {
        dao.insert(baseEvent(hour = 3, origin = 1L))
        dao.insert(baseEvent(hour = 3, origin = 2L))
        val heat = dao.getHourlyHeatmap(0L)
        val h3 = heat.find { it.hourOfDay == 3 }
        assertEquals(2, h3?.count)
    }

    @Test
    fun getActivitySpikes() = runBlocking {
        val w = 1000L
        dao.insert(baseEvent(camera = "A", origin = w))
        dao.insert(baseEvent(camera = "A", origin = w + 1))
        dao.insert(baseEvent(camera = "A", origin = w + 2))
        val spikes = dao.getActivitySpikes(w - 10, w + 10)
        assertEquals(1, spikes.size)
        assertEquals("A", spikes[0].cameraName)
        assertEquals(3, spikes[0].count)
    }

    @Test
    fun getLatest() = runBlocking {
        dao.insert(baseEvent(origin = 10L))
        dao.insert(baseEvent(origin = 50L))
        assertEquals(50L, dao.getLatest()?.originTimestamp)
    }
}
