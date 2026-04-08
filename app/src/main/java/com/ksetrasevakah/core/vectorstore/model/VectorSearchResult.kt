package com.ksetrasevakah.core.vectorstore.model

data class VectorSearchResult(
    val text: String,
    val score: Float,
    val metadata: Map<String, String> = emptyMap()
)
