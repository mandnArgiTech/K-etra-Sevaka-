package com.ksetrasevakah.core.vectorstore

import com.ksetrasevakah.core.common.Result
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RagPipelineTest {

    private lateinit var embeddingGenerator: EmbeddingGenerator
    private lateinit var vectorStoreManager: VectorStoreManager
    private lateinit var pipeline: RagPipeline

    @BeforeEach
    fun setup() {
        embeddingGenerator = mockk()
        vectorStoreManager = mockk(relaxed = true)
        pipeline = RagPipeline(embeddingGenerator, vectorStoreManager)
    }

    @Test
    fun `ingest stores document with embedding`() = runTest {
        val embedding = floatArrayOf(1f, 0f, 0f)
        coEvery { embeddingGenerator.generate("test doc") } returns Result.Success(embedding)

        val result = pipeline.ingest("test doc")

        assertTrue(result is Result.Success)
        verify { vectorStoreManager.addDocument("test doc", embedding, emptyMap()) }
    }

    @Test
    fun `ingest returns error when embedding fails`() = runTest {
        coEvery { embeddingGenerator.generate(any()) } returns Result.Error("Embedding failed")

        val result = pipeline.ingest("test doc")

        assertTrue(result is Result.Error)
    }

    @Test
    fun `query returns formatted results`() = runTest {
        val embedding = floatArrayOf(1f, 0f, 0f)
        coEvery { embeddingGenerator.generate("query") } returns Result.Success(embedding)
        coEvery { vectorStoreManager.search(embedding, any()) } returns listOf(
            com.ksetrasevakah.core.vectorstore.model.VectorSearchResult("doc1", 0.95f),
            com.ksetrasevakah.core.vectorstore.model.VectorSearchResult("doc2", 0.80f)
        )

        val result = pipeline.query("query")

        assertTrue(result is Result.Success)
        val data = (result as Result.Success).data
        assertTrue(data.contains("doc1"))
        assertTrue(data.contains("doc2"))
    }

    @Test
    fun `query returns empty string when no results`() = runTest {
        val embedding = floatArrayOf(1f, 0f, 0f)
        coEvery { embeddingGenerator.generate("query") } returns Result.Success(embedding)
        coEvery { vectorStoreManager.search(embedding, any()) } returns emptyList()

        val result = pipeline.query("query")

        assertTrue(result is Result.Success)
        assertEquals("", (result as Result.Success).data)
    }
}
