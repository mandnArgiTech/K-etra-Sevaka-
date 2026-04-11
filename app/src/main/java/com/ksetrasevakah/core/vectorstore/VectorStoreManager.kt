package com.ksetrasevakah.core.vectorstore

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.database.dao.VectorDocumentDao
import com.ksetrasevakah.core.database.entity.VectorDocumentEntity
import com.ksetrasevakah.core.vectorstore.model.VectorSearchResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.math.sqrt

class VectorStoreManager @Inject constructor(
    private val vectorDocumentDao: VectorDocumentDao
) {

    private val documents = mutableListOf<StoredDocument>()
    private val loadMutex = Mutex()
    private var loaded = false

    suspend fun ensureLoaded() {
        loadMutex.withLock {
            if (loaded) return
            vectorDocumentDao.getAll().forEach { entity ->
                documents.add(
                    StoredDocument(
                        text = entity.text,
                        embedding = entity.embedding.toFloatArrayLittleEndian(),
                        metadata = jsonToMetadataMap(entity.metadataJson)
                    )
                )
            }
            loaded = true
        }
    }

    suspend fun addDocument(text: String, embedding: FloatArray, metadata: Map<String, String> = emptyMap()) {
        ensureLoaded()
        val json = metadataToJson(metadata)
        vectorDocumentDao.insert(
            VectorDocumentEntity(
                text = text,
                embedding = embedding.toLittleEndianByteArray(),
                metadataJson = json
            )
        )
        documents.add(StoredDocument(text, embedding, metadata))
    }

    suspend fun search(
        queryEmbedding: FloatArray,
        topK: Int = Constants.VECTOR_SEARCH_TOP_K,
        namespaces: Set<String>? = null
    ): List<VectorSearchResult> {
        ensureLoaded()
        if (documents.isEmpty()) return emptyList()

        val pool = if (namespaces.isNullOrEmpty()) {
            documents
        } else {
            documents.filter { doc ->
                doc.metadata["module"] in namespaces
            }
        }
        if (pool.isEmpty()) return emptyList()

        return pool
            .map { doc ->
                VectorSearchResult(
                    text = doc.text,
                    score = cosineSimilarity(queryEmbedding, doc.embedding),
                    metadata = doc.metadata
                )
            }
            .sortedByDescending { it.score }
            .take(topK)
    }

    suspend fun clear() {
        loadMutex.withLock {
            vectorDocumentDao.deleteAll()
            documents.clear()
            loaded = true
        }
    }

    suspend fun size(): Int {
        ensureLoaded()
        return documents.size
    }

    internal fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        require(a.size == b.size) { "Vectors must have the same dimension" }
        var dotProduct = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dotProduct += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        val denominator = sqrt(normA) * sqrt(normB)
        return if (denominator == 0f) 0f else dotProduct / denominator
    }

    private data class StoredDocument(
        val text: String,
        val embedding: FloatArray,
        val metadata: Map<String, String>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is StoredDocument) return false
            return text == other.text && embedding.contentEquals(other.embedding) && metadata == other.metadata
        }

        override fun hashCode(): Int {
            var result = text.hashCode()
            result = 31 * result + embedding.contentHashCode()
            result = 31 * result + metadata.hashCode()
            return result
        }
    }
}
