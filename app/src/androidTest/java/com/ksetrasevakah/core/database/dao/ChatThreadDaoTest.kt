package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.ChatThreadEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatThreadDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var dao: ChatThreadDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.chatThreadDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndGetAllSortedByLastMessage() = runBlocking {
        val t1 = 100L
        val t2 = 200L
        dao.insert(ChatThreadEntity(title = "A", lastMessageAt = t1))
        dao.insert(ChatThreadEntity(title = "B", lastMessageAt = t2))
        val list = dao.getAll().first()
        assertEquals("B", list[0].title)
        assertEquals("A", list[1].title)
    }

    @Test
    fun getById() = runBlocking {
        val id = dao.insert(ChatThreadEntity(title = "T", lastMessageAt = 1L))
        val row = dao.getById(id)
        assertEquals("T", row?.title)
    }

    @Test
    fun updateThread() = runBlocking {
        val id = dao.insert(ChatThreadEntity(title = "Old", lastMessageAt = 1L))
        dao.update(id, "New", "preview", 99L)
        assertEquals("New", dao.getById(id)?.title)
        assertEquals(99L, dao.getById(id)?.lastMessageAt)
    }

    @Test
    fun deleteThread() = runBlocking {
        val id = dao.insert(ChatThreadEntity(title = "X", lastMessageAt = 1L))
        dao.delete(id)
        assertNull(dao.getById(id))
    }
}
