package com.ksetrasevakah.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ksetrasevakah.core.database.KsetraDatabase
import com.ksetrasevakah.core.database.entity.ChatMessageEntity
import com.ksetrasevakah.core.database.entity.ChatThreadEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatMessageDaoTest {

    private lateinit var db: KsetraDatabase
    private lateinit var threadDao: ChatThreadDao
    private lateinit var messageDao: ChatMessageDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KsetraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        threadDao = db.chatThreadDao()
        messageDao = db.chatMessageDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    private suspend fun createThread(): Long =
        threadDao.insert(ChatThreadEntity(title = "thr", lastMessageAt = 1L))

    @Test
    fun insertAndObserveThreadMessages() = runBlocking {
        val tid = createThread()
        messageDao.insert(ChatMessageEntity(threadId = tid, role = "user", text = "hi", createdAt = 10L))
        messageDao.insert(ChatMessageEntity(threadId = tid, role = "bot", text = "yo", createdAt = 20L))
        val list = messageDao.getMessagesForThread(tid).first()
        assertEquals(2, list.size)
        assertEquals("hi", list[0].text)
        assertEquals("yo", list[1].text)
    }

    @Test
    fun getMessagesPaginated() = runBlocking {
        val tid = createThread()
        repeat(5) { i ->
            messageDao.insert(ChatMessageEntity(threadId = tid, role = "u", text = "$i", createdAt = i.toLong()))
        }
        val page = messageDao.getMessagesPaginated(tid, limit = 2, offset = 0)
        assertEquals(2, page.size)
    }

    @Test
    fun getRecentMessages() = runBlocking {
        val tid = createThread()
        messageDao.insert(ChatMessageEntity(threadId = tid, role = "u", text = "a", createdAt = 1L))
        messageDao.insert(ChatMessageEntity(threadId = tid, role = "u", text = "b", createdAt = 2L))
        val recent = messageDao.getRecentMessages(tid, limit = 1)
        assertEquals(1, recent.size)
        assertEquals("b", recent[0].text)
    }

    @Test
    fun deleteForThread() = runBlocking {
        val tid = createThread()
        messageDao.insert(ChatMessageEntity(threadId = tid, role = "u", text = "x", createdAt = 1L))
        messageDao.deleteForThread(tid)
        assertEquals(0, messageDao.getMessagesForThread(tid).first().size)
    }
}
