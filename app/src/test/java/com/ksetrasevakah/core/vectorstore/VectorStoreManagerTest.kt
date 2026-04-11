package com.ksetrasevakah.core.vectorstore

import com.ksetrasevakah.core.database.dao.VectorDocumentDao
import com.ksetrasevakah.core.database.entity.VectorDocumentEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VectorStoreManagerTest {

    private lateinit var dao: VectorDocumentDao
    private lateinit var manager: VectorStoreManager

    @BeforeEach
    fun setup() {
        dao = mockk(relaxed = true)
        coEvery { dao.getAll() } returns emptyList()
        coEvery { dao.insert(any()) } returns 1L
        coEvery { dao.deleteAll() } returns Unit
        manager = VectorStoreManager(dao)
    }

    @Test
    fun `search returns empty list when store is empty`() = runBlocking {
        val results = manager.search(floatArrayOf(1f, 0f, 0f))
        assertTrue(results.isEmpty())
    }

    @Test
    fun `addDocument increases store size`() = runBlocking {
        manager.addDocument("doc1", floatArrayOf(1f, 0f, 0f))
        manager.addDocument("doc2", floatArrayOf(0f, 1f, 0f))

        assertEquals(2, manager.size())
        coVerify(atLeast = 2) { dao.insert(any()) }
    }

    @Test
    fun `search returns most similar documents first`() = runBlocking {
        manager.addDocument("similar", floatArrayOf(1f, 0f, 0f))
        manager.addDocument("different", floatArrayOf(0f, 1f, 0f))
        manager.addDocument("orthogonal", floatArrayOf(0f, 0f, 1f))

        val results = manager.search(floatArrayOf(1f, 0f, 0f), topK = 2)

        assertEquals(2, results.size)
        assertEquals("similar", results[0].text)
        assertTrue(results[0].score > results[1].score)
    }

    @Test
    fun `cosineSimilarity returns 1 for identical vectors`() {
        val v = floatArrayOf(1f, 2f, 3f)
        val similarity = manager.cosineSimilarity(v, v)
        assertTrue(similarity > 0.999f)
    }

    @Test
    fun `clear removes all documents`() = runBlocking {
        manager.addDocument("doc1", floatArrayOf(1f, 0f))
        manager.addDocument("doc2", floatArrayOf(0f, 1f))
        manager.clear()

        assertEquals(0, manager.size())
        coVerify { dao.deleteAll() }
    }

    @Test
    fun `reloads from dao when new manager after persisted docs`() = runBlocking {
        val emb = floatArrayOf(1f, 0f, 0f)
        val entity = VectorDocumentEntity(
            id = 1L,
            text = "from-db",
            embedding = emb.toLittleEndianByteArray(),
            metadataJson = "{}"
        )
        val dao2 = mockk<VectorDocumentDao>()
        coEvery { dao2.getAll() } returns listOf(entity)
        val m2 = VectorStoreManager(dao2)
        val results = m2.search(emb)
        assertEquals(1, results.size)
        assertEquals("from-db", results[0].text)
    }
}
