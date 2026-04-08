package com.ksetrasevakah.core.vectorstore

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.vectorstore.model.VectorSearchResult
import javax.inject.Inject
import kotlin.math.sqrt

class VectorStoreManager @Inject constructor() {

    private val documents = mutableListOf<StoredDocument>()

    fun addDocument(text: String, embedding: FloatArray, metadata: Map<String, String> = emptyMap()) {
        documents.add(StoredDocument(text, embedding, metadata))
    }

    fun search(queryEmbedding: FloatArray, topK: Int = Constants.VECTOR_SEARCH_TOP_K): List<VectorSearchResult> {
        if (documents.isEmpty()) return emptyList()

        return documents
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

    fun clear() {
        documents.clear()
    }

    fun size(): Int = documents.size

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
