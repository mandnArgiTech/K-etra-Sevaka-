package com.ksetrasevakah.core.common

/**
 * App-wide constants shared across all modules and layers.
 */
object Constants {
    const val TARO_PANEL_NUMBER = "7093652065"

    /** Logical model ID for the on-device chat / orchestrator LLM. */
    const val ORCHESTRATOR_MODEL_ID = "gemma3-1b-it"

    /** LiteRT-LM model file stored under `filesDir/models/` after first-run download. */
    const val ORCHESTRATOR_MODEL_FILENAME = "gemma3-1b-it-int4.litertlm"

    /** HuggingFace litert-community download URL (must match an actual file in the repo). */
    const val ORCHESTRATOR_MODEL_DOWNLOAD_URL =
        "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/gemma3-1b-it-int4.litertlm"

    /** Sentence embedding model (ONNX) + vocab for RAG */
    const val EMBEDDING_ONNX_FILENAME = "minilm_l6_v2_model.onnx"
    const val EMBEDDING_VOCAB_FILENAME = "vocab.txt"
    const val EMBEDDING_ONNX_DOWNLOAD_URL =
        "https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/onnx/model.onnx"
    const val EMBEDDING_VOCAB_DOWNLOAD_URL =
        "https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main/vocab.txt"
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
