package com.ksetrasevakah.core.vectorstore

import com.ksetrasevakah.core.common.Result
import javax.inject.Inject
import kotlin.random.Random

class EmbeddingGenerator @Inject constructor() {

    suspend fun generate(text: String): Result<FloatArray> {
        return try {
            // Stub: returns deterministic pseudo-random vectors based on text hash
            val seed = text.hashCode().toLong()
            val random = Random(seed)
            val embedding = FloatArray(EMBEDDING_DIM) { random.nextFloat() * 2f - 1f }
            val norm = kotlin.math.sqrt(embedding.sumOf { (it * it).toDouble() }).toFloat()
            if (norm > 0f) {
                for (i in embedding.indices) embedding[i] /= norm
            }
            Result.Success(embedding)
        } catch (e: Exception) {
            Result.Error("Embedding generation failed: ${e.message}", e)
        }
    }

    companion object {
        const val EMBEDDING_DIM = 384
    }
}
