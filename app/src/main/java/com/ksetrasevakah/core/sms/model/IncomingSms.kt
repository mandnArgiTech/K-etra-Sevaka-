package com.ksetrasevakah.core.sms.model

data class IncomingSms(
    val sender: String,
    val body: String,
    val timestamp: Long
)
