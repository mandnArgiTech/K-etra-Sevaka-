package com.ksetrasevakah.core.sms

import com.ksetrasevakah.core.ai.SmsTelemetryProcessor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Re-processes historical panel SMS through the same pipeline as live [IngestionService] / setup.
 */
@Singleton
class SmsBulkIngestor @Inject constructor(
    private val smsHistoryReader: SmsHistoryReader,
    private val smsTelemetryProcessor: SmsTelemetryProcessor
) {

    /**
     * Reads up to [limit] inbox messages for the panel number and runs each through [SmsTelemetryProcessor].
     * @param onProgress invoked after each message with (index 1-based, total).
     */
    suspend fun reingestPanelHistory(
        limit: Int = 500,
        onProgress: (done: Int, total: Int) -> Unit = { _, _ -> }
    ): Result<Int> {
        return try {
            val messages = smsHistoryReader.readPanelMessages(limit)
            if (messages.isEmpty()) {
                return Result.success(0)
            }
            messages.forEachIndexed { index, sms ->
                smsTelemetryProcessor.process(sms.body, sms.timestamp)
                onProgress(index + 1, messages.size)
            }
            Result.success(messages.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
