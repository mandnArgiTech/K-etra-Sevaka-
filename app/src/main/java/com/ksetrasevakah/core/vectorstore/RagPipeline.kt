package com.ksetrasevakah.core.vectorstore

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import javax.inject.Inject

class RagPipeline @Inject constructor(
    private val embeddingGenerator: EmbeddingGenerator,
    private val vectorStoreManager: VectorStoreManager
) {

    suspend fun ingest(text: String, metadata: Map<String, String> = emptyMap()): Result<Unit> {
        return when (val embeddingResult = embeddingGenerator.generate(text)) {
            is Result.Success -> {
                vectorStoreManager.addDocument(text, embeddingResult.data, metadata)
                Result.Success(Unit)
            }
            is Result.Error -> Result.Error(embeddingResult.message, embeddingResult.throwable)
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun query(
        queryText: String,
        topK: Int = Constants.VECTOR_SEARCH_TOP_K,
        namespaces: Set<String>? = null
    ): Result<String> {
        return when (val embeddingResult = embeddingGenerator.generate(queryText)) {
            is Result.Success -> {
                val embedding = embeddingResult.data
                var results = vectorStoreManager.search(embedding, topK, namespaces)
                // Legacy or untagged documents have no module metadata — fall back so chat RAG still works
                if (results.isEmpty() && namespaces != null) {
                    results = vectorStoreManager.search(embedding, topK, namespaces = null)
                }
                if (results.isEmpty()) {
                    Result.Success("")
                } else {
                    val formatted = results.joinToString("\n\n") { result ->
                        "- ${result.text} (relevance: ${"%.2f".format(result.score)})"
                    }
                    Result.Success(formatted)
                }
            }
            is Result.Error -> Result.Error(embeddingResult.message, embeddingResult.throwable)
            is Result.Loading -> Result.Loading
        }
    }
}
