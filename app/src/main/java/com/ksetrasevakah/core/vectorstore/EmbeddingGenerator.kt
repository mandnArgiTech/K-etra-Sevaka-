package com.ksetrasevakah.core.vectorstore

import com.ksetrasevakah.core.common.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class EmbeddingGenerator @Inject constructor(
    private val embeddingDownloader: EmbeddingDownloader
) {

    private val initMutex = Mutex()
    private var encoder: OnnxMiniLmEncoder? = null
    private var initFailed = false

    /**
     * Produces a 384-dim L2-normalized embedding when ONNX assets are present (after setup download).
     * Falls back to deterministic pseudo-vectors only if the model cannot be loaded.
     */
    suspend fun generate(text: String): Result<FloatArray> {
        return try {
            withContext(Dispatchers.Default) {
                val enc = getOrCreateEncoder()
                if (enc != null) {
                    Result.Success(enc.embed(text))
                } else {
                    Result.Success(fallbackEmbedding(text))
                }
            }
        } catch (e: Exception) {
            Result.Error("Embedding generation failed: ${e.message}", e)
        }
    }

    private suspend fun getOrCreateEncoder(): OnnxMiniLmEncoder? {
        initMutex.withLock {
            if (initFailed) return null
            encoder?.let { return it }
            val onnx = embeddingDownloader.onnxFile()
            val vocab = embeddingDownloader.vocabFile()
            if (!onnx.exists() || !vocab.exists()) {
                return null
            }
            return try {
                val lines = vocab.readLines()
                OnnxMiniLmEncoder(onnx, lines).also { encoder = it }
            } catch (e: Exception) {
                initFailed = true
                encoder?.close()
                encoder = null
                null
            }
        }
    }

    private fun fallbackEmbedding(text: String): FloatArray {
        val seed = text.hashCode().toLong()
        val random = Random(seed)
        val embedding = FloatArray(EMBEDDING_DIM) { random.nextFloat() * 2f - 1f }
        val norm = kotlin.math.sqrt(embedding.sumOf { (it * it).toDouble() }).toFloat()
        if (norm > 0f) {
            for (i in embedding.indices) embedding[i] /= norm
        }
        return embedding
    }

    companion object {
        const val EMBEDDING_DIM = 384
    }
}
