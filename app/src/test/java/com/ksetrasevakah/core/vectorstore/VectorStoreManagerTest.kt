package com.ksetrasevakah.core.vectorstore

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VectorStoreManagerTest {

    private lateinit var manager: VectorStoreManager

    @BeforeEach
    fun setup() {
        manager = VectorStoreManager()
    }

    @Test
    fun `search returns empty list when store is empty`() {
        val results = manager.search(floatArrayOf(1f, 0f, 0f))
        assertTrue(results.isEmpty())
    }

    @Test
    fun `addDocument increases store size`() {
        manager.addDocument("doc1", floatArrayOf(1f, 0f, 0f))
        manager.addDocument("doc2", floatArrayOf(0f, 1f, 0f))

        assertEquals(2, manager.size())
    }

    @Test
    fun `search returns most similar documents first`() {
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
    fun `clear removes all documents`() {
        manager.addDocument("doc1", floatArrayOf(1f, 0f))
        manager.addDocument("doc2", floatArrayOf(0f, 1f))
        manager.clear()

        assertEquals(0, manager.size())
    }
}
