package com.ksetrasevakah.core.vectorstore

import android.content.Context
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmbeddingDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {

    private fun dir(): File = File(context.filesDir, EMBED_SUBDIR).apply { mkdirs() }

    fun onnxFile(): File = File(dir(), Constants.EMBEDDING_ONNX_FILENAME)
    fun vocabFile(): File = File(dir(), Constants.EMBEDDING_VOCAB_FILENAME)

    /**
     * @param onDownloadProgress invoked on IO thread: file index, total files, bytes read, content length if known.
     */
    suspend fun ensureEmbeddingAssets(
        minOnnxBytes: Long = 500_000L,
        onDownloadProgress: (fileIndex: Int, fileCount: Int, bytesRead: Long, contentLength: Long?) -> Unit =
            { _, _, _, _ -> }
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val onnx = onnxFile()
        val vocab = vocabFile()
        if (onnx.exists() && onnx.length() >= minOnnxBytes && vocab.exists() && vocab.length() > 1000L) {
            return@withContext Result.Success(Unit)
        }
        val fileCount = 2
        when (
            val v = downloadFile(Constants.EMBEDDING_VOCAB_DOWNLOAD_URL, vocab) { read, total ->
                onDownloadProgress(0, fileCount, read, total)
            }
        ) {
            is Result.Error -> return@withContext v
            else -> Unit
        }
        when (
            val m = downloadFile(Constants.EMBEDDING_ONNX_DOWNLOAD_URL, onnx) { read, total ->
                onDownloadProgress(1, fileCount, read, total)
            }
        ) {
            is Result.Error -> return@withContext m
            else -> Unit
        }
        Result.Success(Unit)
    }

    private fun downloadFile(
        url: String,
        outFile: File,
        onProgress: ((bytesRead: Long, contentLength: Long?) -> Unit)? = null
    ): Result<Unit> {
        val temp = File(outFile.parentFile, outFile.name + ".part")
        if (temp.exists()) temp.delete()
        return try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "KsetraSevakah/1.0 (Android)")
                .build()
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.Error("HTTP ${response.code} for $url")
                }
                val body = response.body ?: return Result.Error("Empty body")
                val contentLength = body.contentLength().takeIf { it > 0 }
                FileOutputStream(temp).use { fos ->
                    val buffer = ByteArray(STREAM_BUFFER)
                    var total = 0L
                    body.byteStream().use { input ->
                        while (true) {
                            val read = input.read(buffer)
                            if (read == -1) break
                            fos.write(buffer, 0, read)
                            total += read
                            onProgress?.invoke(total, contentLength)
                        }
                    }
                }
                if (outFile.exists()) outFile.delete()
                if (!temp.renameTo(outFile)) {
                    temp.copyTo(outFile, overwrite = true)
                    temp.delete()
                }
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error("Download failed: ${e.message}", e)
        }
    }

    companion object {
        private const val EMBED_SUBDIR = "embedding"
        private const val STREAM_BUFFER = 8192
    }
}
