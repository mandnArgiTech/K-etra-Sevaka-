package com.ksetrasevakah.feature.pumpiq.chat.domain.model

data class ChatThread(
    val id: Long,
    val title: String,
    val preview: String,
    val createdAt: Long,
    val lastMessageAt: Long
)
