package com.ksetrasevakah.core.common

/**
 * App-wide constants shared across all modules and layers.
 */
object Constants {
    const val TARO_PANEL_NUMBER = "070936 52065"
    const val INGESTION_MODEL_ID = "Qwen2.5-0.5B-Instruct-q4f16_1-MLC"
    const val ORCHESTRATOR_MODEL_ID = "Qwen2.5-3B-Instruct-q4f16_1-MLC"
    const val DB_NAME = "ksetra_sevakah_db"
    const val VECTOR_DB_DIR = "vector_store"
    const val BACKUP_FOLDER_NAME = "KsetraSevakah_Backup"
    const val SMS_TIMEOUT_SECONDS = 30L
    const val PREDICTION_WINDOW_DAYS = 14
    const val FORGOT_OFF_THRESHOLD_MINUTES = 45
    const val MAX_CHAT_CONTEXT_MESSAGES = 10
    const val VECTOR_SEARCH_TOP_K = 5
    const val RAG_MODULE_PUMPIQ = "PumpIQ"
    const val RAG_MODULE_SURAKSHA = "Suraksha"
    /** Rough cap (~4096 tokens) for assembled system prompt text */
    const val SYSTEM_PROMPT_MAX_CHARS = 14_000
    /** Cap RAG embedding input from chat to bound latency and cost */
    const val RAG_QUERY_MAX_CHARS = 512
    const val BACKUP_SCHEDULE_HOUR = 2

    const val TAPO_PACKAGE_NAME = "com.tplink.iot"
    const val NOTIFICATION_WHEN_MAX_AGE_MS = 300_000L
    const val ACTIVITY_SPIKE_WINDOW_MS = 300_000L
    const val COORDINATED_WINDOW_MS = 120_000L
    const val ACTIVITY_SPIKE_THRESHOLD = 3
    const val CRITICAL_ALARM_DURATION_MS = 15_000L
    const val CRITICAL_OVERLAY_TIMEOUT_MS = 60_000L
    const val CROSS_MODULE_CORRELATION_WINDOW_MS = 300_000L
}
