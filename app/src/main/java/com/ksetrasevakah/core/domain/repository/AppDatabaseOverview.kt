package com.ksetrasevakah.core.domain.repository

/**
 * Read-only snapshot of on-device structured data for user-visible diagnostics.
 */
data class AppDatabaseOverview(
    val telemetryRowCount: Long,
    val telemetryOldestMs: Long?,
    val telemetryNewestMs: Long?,
    /** Device wall-clock when the app last finished writing an SMS row to `telemetry_log`. */
    val lastPipelineIngestWallClockMs: Long?,
    val vectorChunkCount: Long,
    val vectorOldestMs: Long?,
    val vectorNewestMs: Long?,
    val workerActivityRowCount: Long,
    val faultRowCount: Long,
    val chatThreadCount: Long,
    val chatMessageCount: Long,
    val securityEventCount: Long,
    val backupLogRowCount: Long,
    val modelsDownloaded: Boolean,
    val firstRunComplete: Boolean
)
