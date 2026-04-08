package com.ksetrasevakah.core.sms

import android.content.Intent
import android.provider.Telephony
import com.ksetrasevakah.core.sms.model.IncomingSms

object SmsParser {

    fun extractFromIntent(intent: Intent): List<IncomingSms> {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            ?: return emptyList()

        return messages
            .groupBy { it.originatingAddress ?: "" }
            .map { (sender, parts) ->
                IncomingSms(
                    sender = sender,
                    body = parts.joinToString("") { it.messageBody ?: "" },
                    timestamp = parts.first().timestampMillis
                )
            }
    }
}
