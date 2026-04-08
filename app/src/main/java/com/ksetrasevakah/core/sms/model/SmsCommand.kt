package com.ksetrasevakah.core.sms.model

sealed class SmsCommand(val text: String) {
    data object Start : SmsCommand("START")
    data object Stop : SmsCommand("STOP")
    data object Status : SmsCommand("STATUS")
}
