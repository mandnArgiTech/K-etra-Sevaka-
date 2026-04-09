package com.ksetrasevakah.core.notification

fun interface NotificationDismisser {
    fun dismiss(sbnKey: String)
}
