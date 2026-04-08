# E03-S02: SMS Receive Engine — BroadcastReceiver & Filtering

**Epic:** 03 — SMS Engine  
**Size:** M (2-4h)  
**Dependencies:** E03-S01

## Description
Implement inbound SMS processing: BroadcastReceiver intercepts SMS, **strictly filters** to only process messages from `070936 52065`, and passes to Ingestion Service.

## Acceptance Criteria
- [ ] AC1: `SmsReceiver` extends `BroadcastReceiver` and registers for `SMS_RECEIVED`
- [ ] AC2: **Critical:** Messages from any sender OTHER than `070936 52065` are silently ignored
- [ ] AC3: Messages from `070936 52065` are forwarded to `IngestionService` via Intent
- [ ] AC4: `SmsParser` extracts sender number and message body from `SmsMessage[]`
- [ ] AC5: Receiver registered in `AndroidManifest.xml` with correct intent-filter and priority
- [ ] AC6: Receiver handles multi-part SMS correctly (concatenates parts)
- [ ] AC7: `RECEIVE_SMS` and `READ_SMS` permissions declared in manifest
- [ ] AC8: Unit tests verify filtering logic — only target number passes through

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/sms/
├── SmsReceiver.kt                         # BroadcastReceiver
├── SmsParser.kt                           # Extract sender + body from SMS PDUs
└── model/IncomingSms.kt                   # Data class: sender, body, timestamp

app/src/test/java/com/ksetrasevakah/core/sms/SmsReceiverTest.kt
app/src/test/java/com/ksetrasevakah/core/sms/SmsParserTest.kt
```

## Implementation Details

### SmsReceiver — Critical Filtering
```kotlin
class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val messages = SmsParser.parse(intent) ?: return
        
        // CRITICAL FILTER: Only process SMS from Taro Panel
        val taroMessages = messages.filter { 
            it.sender.replace(" ", "") == Constants.TARO_PANEL_NUMBER.replace(" ", "")
        }
        if (taroMessages.isEmpty()) return // Silently ignore all other SMS
        
        taroMessages.forEach { sms ->
            val serviceIntent = Intent(context, IngestionService::class.java).apply {
                putExtra(IngestionService.EXTRA_SMS_BODY, sms.body)
                putExtra(IngestionService.EXTRA_SMS_TIMESTAMP, sms.timestamp)
            }
            context.startForegroundService(serviceIntent)
        }
    }
}
```

### SmsParser
```kotlin
object SmsParser {
    fun parse(intent: Intent): List<IncomingSms>? {
        val bundle = intent.extras ?: return null
        val pdus = bundle.get("pdus") as? Array<*> ?: return null
        val format = bundle.getString("format") ?: return null
        return pdus.mapNotNull { pdu ->
            val smsMessage = SmsMessage.createFromPdu(pdu as ByteArray, format)
            IncomingSms(
                sender = smsMessage.displayOriginatingAddress ?: return@mapNotNull null,
                body = smsMessage.messageBody ?: return@mapNotNull null,
                timestamp = smsMessage.timestampMillis
            )
        }
    }
}
```

## Test Requirements

### SmsReceiverTest
- SMS from `070936 52065` → service intent fired
- SMS from `+1234567890` → no service intent (ignored)
- SMS from `07093652065` (no space) → still matches and processes
- Null/empty intent → no crash, returns early

### SmsParserTest
- Valid PDU → returns IncomingSms with correct sender, body, timestamp
- Null extras → returns null
- Empty PDUs array → returns null

## Definition of Done
- Only `070936 52065` SMS gets processed — this is a security requirement
- Manifest has receiver with `SMS_RECEIVED` intent-filter
- All tests pass
