package com.ksetrasevakah.core.ai

import android.content.Context
import android.util.Log
import com.ksetrasevakah.core.ai.model.DownloadProgress
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
class ModelDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {

    private fun modelsDir(): File =
        File(context.filesDir, MODELS_SUBDIR).apply { mkdirs() }

    fun specForModelId(modelId: String): Pair<String, String> = when (modelId) {
        Constants.ORCHESTRATOR_MODEL_ID ->
            Constants.ORCHESTRATOR_MODEL_DOWNLOAD_URL to Constants.ORCHESTRATOR_MODEL_FILENAME
        else -> error("Unknown model id: $modelId")
    }

    fun localFileForModelId(modelId: String): File {
        val (_, fileName) = specForModelId(modelId)
        return File(modelsDir(), fileName)
    }

    suspend fun downloadModelIfNeeded(
        modelId: String,
        minBytes: Long = MIN_VALID_MODEL_BYTES,
        onProgress: (DownloadProgress) -> Unit = {}
    ): Result<File> = withContext(Dispatchers.IO) {
        val (url, fileName) = specForModelId(modelId)
        val outFile = File(modelsDir(), fileName)
        if (outFile.exists() && outFile.length() >= minBytes) {
            onProgress(
                DownloadProgress(
                    bytesRead = outFile.length(),
                    contentLength = outFile.length(),
                    phase = DownloadProgress.Phase.Complete
                )
            )
            return@withContext Result.Success(outFile)
        }

        val tempFile = File(modelsDir(), "$fileName.part")
        if (tempFile.exists()) tempFile.delete()

        try {
            Log.i(TAG, "GET model: $url (file=$fileName)")
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "KsetraSevakah/1.0 (Android)")
                .build()

            val downloadResult = okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    onProgress(
                        DownloadProgress(0L, null, DownloadProgress.Phase.Failed)
                    )
                    val bodySnippet = try {
                        response.body?.string()?.take(800)?.replace("\n", " ") ?: ""
                    } catch (_: Exception) {
                        ""
                    }
                    Log.w(
                        TAG,
                        "Model download failed: HTTP ${response.code} url=$url " +
                            "message=${response.message} bodySnippet=${bodySnippet.take(400)}"
                    )
                    val hint = when (response.code) {
                        401 -> {
                            " Gemma is gated: accept the license on the Hugging Face model page, " +
                                "create a read token at https://huggingface.co/settings/tokens , " +
                                "paste it into the \"Hugging Face read token\" field on this screen, " +
                                "then tap Download again. (Developers can also set HUGGINGFACE_READ_TOKEN " +
                                "in local.properties and rebuild.)"
                        }
                        404 -> {
                            " URL not found (404): wrong filename or repo path. " +
                                "See logcat tag $TAG for full URL and response snippet."
                        }
                        else -> ""
                    }
                    return@use Result.Error("HTTP ${response.code} downloading model.$hint")
                }
                val body = response.body ?: return@use Result.Error("Empty response body")
                val contentLength = body.contentLength().takeIf { it > 0 }

                FileOutputStream(tempFile).use { fos ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var total = 0L
                    body.byteStream().use { input ->
                        while (true) {
                            val read = input.read(buffer)
                            if (read == -1) break
                            fos.write(buffer, 0, read)
                            total += read
                            onProgress(
                                DownloadProgress(
                                    bytesRead = total,
                                    contentLength = contentLength,
                                    phase = DownloadProgress.Phase.Downloading
                                )
                            )
                        }
                    }
                }

                if (outFile.exists()) outFile.delete()
                if (!tempFile.renameTo(outFile)) {
                    tempFile.copyTo(outFile, overwrite = true)
                    tempFile.delete()
                }

                if (!outFile.exists() || outFile.length() < minBytes) {
                    outFile.delete()
                    onProgress(DownloadProgress(0L, null, DownloadProgress.Phase.Failed))
                    return@use Result.Error("Downloaded file too small")
                }

                onProgress(
                    DownloadProgress(
                        bytesRead = outFile.length(),
                        contentLength = outFile.length(),
                        phase = DownloadProgress.Phase.Complete
                    )
                )
                Log.i(TAG, "Model saved: ${outFile.absolutePath} size=${outFile.length()}")
                Result.Success(outFile)
            }
            downloadResult
        } catch (e: Exception) {
            Log.e(TAG, "Model download exception url=$url", e)
            onProgress(DownloadProgress(0L, null, DownloadProgress.Phase.Failed))
            Result.Error("Model download failed: ${e.message}", e)
        } finally {
            if (tempFile.exists()) {
                val outOk = outFile.exists() && outFile.length() >= minBytes
                if (outOk) tempFile.delete()
            }
        }
    }

    companion object {
        private const val TAG = "ModelDownloader"
        private const val MODELS_SUBDIR = "models"
        private const val MIN_VALID_MODEL_BYTES = 1_000_000L
    }
}
