package com.ksetrasevakah.core.ai.model

/**
 * Progress for a single model weight download.
 */
data class DownloadProgress(
    val bytesRead: Long,
    val contentLength: Long?,
    val phase: Phase
) {
    enum class Phase {
        Downloading,
        Complete,
        Failed
    }

    val fraction: Float
        get() {
            val total = contentLength ?: return if (bytesRead > 0) 0f else 0f
            if (total <= 0L) return 0f
            return (bytesRead.toDouble() / total.toDouble()).toFloat().coerceIn(0f, 1f)
        }
}
