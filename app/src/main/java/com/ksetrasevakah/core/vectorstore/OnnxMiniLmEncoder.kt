package com.ksetrasevakah.core.vectorstore

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import com.ksetrasevakah.core.vectorstore.tokenizer.WordPieceTokenizer
import java.io.File
import java.nio.LongBuffer
import kotlin.math.sqrt

/**
 * Runs all-MiniLM-L6-v2 ONNX export: mean-pooled sentence embedding (dim 384).
 */
class OnnxMiniLmEncoder(
    onnxPath: File,
    vocabLines: List<String>
) : AutoCloseable {

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession
    private val tokenizer: WordPieceTokenizer = WordPieceTokenizer.fromVocabLines(vocabLines)
    private val outputName: String

    init {
        val opts = OrtSession.SessionOptions().apply {
            setIntraOpNumThreads(4)
            setInterOpNumThreads(1)
        }
        session = env.createSession(onnxPath.absolutePath, opts)
        outputName = session.outputNames.first()
    }

    fun embed(text: String, maxLength: Int = 128): FloatArray {
        val (inputIds, attentionMask) = tokenizer.encode(text, maxLength)
        val batch = 1L
        val seq = maxLength.toLong()

        val inputIdsTensor = OnnxTensor.createTensor(env, LongBuffer.wrap(inputIds), longArrayOf(batch, seq))
        val attentionTensor = OnnxTensor.createTensor(env, LongBuffer.wrap(attentionMask), longArrayOf(batch, seq))

        val feeds = java.util.LinkedHashMap<String, OnnxTensor>()
        val inNames = session.inputNames
        val idName = when {
            "input_ids" in inNames -> "input_ids"
            else -> inNames.first()
        }
        val maskName = when {
            "attention_mask" in inNames -> "attention_mask"
            inNames.size > 1 -> inNames.first { it != idName }
            else -> idName
        }
        feeds[idName] = inputIdsTensor
        if (maskName != idName) {
            feeds[maskName] = attentionTensor
        }
        if ("token_type_ids" in inNames) {
            val zeros = LongArray(maxLength) { 0L }
            feeds["token_type_ids"] = OnnxTensor.createTensor(
                env,
                LongBuffer.wrap(zeros),
                longArrayOf(batch, seq)
            )
        }

        val tokenTypeTensor = feeds["token_type_ids"]
        return try {
            session.run(feeds).use { results ->
                val onnxValue = results[outputName].orElseThrow()
                val tensor = onnxValue as OnnxTensor
                @Suppress("UNCHECKED_CAST")
                val value = tensor.getValue() as Array<Array<FloatArray>>
                meanPool(value[0], attentionMask, maxLength)
            }
        } finally {
            inputIdsTensor.close()
            attentionTensor.close()
            tokenTypeTensor?.close()
        }
    }

    private fun meanPool(sequence: Array<FloatArray>, mask: LongArray, maxLength: Int): FloatArray {
        val hidden = sequence[0].size
        val sums = FloatArray(hidden)
        var count = 0
        for (t in 0 until minOf(sequence.size, maxLength)) {
            if (mask.getOrElse(t) { 0L } == 0L) continue
            val row = sequence[t]
            for (i in row.indices) sums[i] += row[i]
            count++
        }
        if (count == 0) return FloatArray(hidden)
        for (i in sums.indices) sums[i] /= count.toFloat()
        var norm = 0f
        for (v in sums) norm += v * v
        norm = sqrt(norm)
        if (norm > 1e-6f) {
            for (i in sums.indices) sums[i] /= norm
        }
        return sums
    }

    override fun close() {
        session.close()
        env.close()
    }
}
