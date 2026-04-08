package com.ksetrasevakah.core.sms

import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import com.ksetrasevakah.core.sms.model.IncomingSms
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SmsParserTest {

    @BeforeEach
    fun setup() {
        mockkStatic(Telephony.Sms.Intents::class)
    }

    @AfterEach
    fun teardown() {
        unmockkStatic(Telephony.Sms.Intents::class)
    }

    @Test
    fun `extractFromIntent returns empty list when no messages`() {
        val intent = mockk<Intent>()
        every { Telephony.Sms.Intents.getMessagesFromIntent(intent) } returns null

        val result = SmsParser.extractFromIntent(intent)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `extractFromIntent parses single SMS`() {
        val intent = mockk<Intent>()
        val smsMessage = mockk<SmsMessage>()
        every { smsMessage.originatingAddress } returns "+91070936 52065"
        every { smsMessage.messageBody } returns "MOTOR ON confirmed"
        every { smsMessage.timestampMillis } returns 1000L

        every { Telephony.Sms.Intents.getMessagesFromIntent(intent) } returns arrayOf(smsMessage)

        val result = SmsParser.extractFromIntent(intent)

        assertEquals(1, result.size)
        assertEquals("+91070936 52065", result[0].sender)
        assertEquals("MOTOR ON confirmed", result[0].body)
        assertEquals(1000L, result[0].timestamp)
    }

    @Test
    fun `extractFromIntent concatenates multipart SMS from same sender`() {
        val intent = mockk<Intent>()
        val part1 = mockk<SmsMessage>()
        val part2 = mockk<SmsMessage>()
        every { part1.originatingAddress } returns "+910709365206"
        every { part1.messageBody } returns "MOTOR ON "
        every { part1.timestampMillis } returns 1000L
        every { part2.originatingAddress } returns "+910709365206"
        every { part2.messageBody } returns "confirmed"
        every { part2.timestampMillis } returns 1001L

        every { Telephony.Sms.Intents.getMessagesFromIntent(intent) } returns arrayOf(part1, part2)

        val result = SmsParser.extractFromIntent(intent)

        assertEquals(1, result.size)
        assertEquals("MOTOR ON confirmed", result[0].body)
    }
}
