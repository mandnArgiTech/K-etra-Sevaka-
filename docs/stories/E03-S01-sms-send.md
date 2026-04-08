# E03-S01: SMS Send Engine

**Epic:** 03 — SMS Engine  
**Size:** M (2-4h)  
**Dependencies:** E02-S02

## Description
Implement outbound SMS capability: send START, STOP, STATUS commands to Taro panel number `070936 52065`. Includes delivery confirmation via PendingIntent.

## Acceptance Criteria
- [ ] AC1: `SmsCommandSender` class wraps Android `SmsManager` with Hilt injection
- [ ] AC2: `sendCommand(command: SmsCommand)` sends SMS **only** to `070936 52065`
- [ ] AC3: `SmsCommand` is a sealed class: `Start`, `Stop`, `Status`
- [ ] AC4: Delivery `PendingIntent` registered — `SmsSentReceiver` BroadcastReceiver fires on send result
- [ ] AC5: `SendSmsUseCase` validates motor state before sending (no START if already ON)
- [ ] AC6: Returns `Result.Success` on SMS accepted, `Result.Error` on failure/permission denied
- [ ] AC7: Permission check for `SEND_SMS` — returns `Result.Error` if not granted
- [ ] AC8: Unit tests pass with mocked SmsManager

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/sms/
├── model/SmsCommand.kt                    # sealed class: Start, Stop, Status
├── SmsCommandSender.kt                    # SmsManager wrapper
├── SmsSentReceiver.kt                     # BroadcastReceiver for send confirmation
└── di/SmsModule.kt                        # Hilt provider

app/src/main/java/com/ksetrasevakah/feature/pumpiq/domain/
└── usecase/SendSmsCommandUseCase.kt       # Validates state, then sends

app/src/test/java/com/ksetrasevakah/core/sms/SmsCommandSenderTest.kt
app/src/test/java/com/ksetrasevakah/feature/pumpiq/domain/usecase/SendSmsCommandUseCaseTest.kt
```

## Implementation Details

### SmsCommand.kt
```kotlin
sealed class SmsCommand(val text: String) {
    data object Start : SmsCommand("START")
    data object Stop : SmsCommand("STOP")
    data object Status : SmsCommand("STATUS")
}
```

### SmsCommandSender (key logic)
```kotlin
// CRITICAL: Only send to TARO_PANEL_NUMBER
fun sendCommand(command: SmsCommand): Result<Unit> {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) 
        != PackageManager.PERMISSION_GRANTED) {
        return Result.Error("SMS permission not granted")
    }
    try {
        val sentPI = PendingIntent.getBroadcast(context, 0, 
            Intent(ACTION_SMS_SENT), PendingIntent.FLAG_IMMUTABLE)
        smsManager.sendTextMessage(
            Constants.TARO_PANEL_NUMBER, null, command.text, sentPI, null
        )
        return Result.Success(Unit)
    } catch (e: Exception) {
        return Result.Error("Failed to send SMS: ${e.message}", e)
    }
}
```

### SendSmsCommandUseCase — Validation
```kotlin
// If command is Start and motor is already ON → Error
// If command is Stop and motor is already OFF → Error
// If any pending command exists → Error ("Wait for previous command")
// Otherwise → sender.sendCommand() + update motor state to PENDING
```

## Test Requirements

### SmsCommandSenderTest (Mockk SmsManager)
- Verify `sendTextMessage` called with correct number `070936 52065`
- Verify `sendTextMessage` called with correct command text "START"/"STOP"/"STATUS"
- Verify PendingIntent created for delivery confirmation
- Verify Result.Error when permission not granted

### SendSmsCommandUseCaseTest (Mock repos)
- Start when OFF → Success, motor state → PENDING_START
- Start when ON → Error("Motor already running")
- Stop when ON → Success, motor state → PENDING_STOP
- Stop when OFF → Error("Motor already off")
- Any command when PENDING → Error("Wait for previous command")

## Definition of Done
- SMS only targets `070936 52065` (hardcoded, never parameterized)
- Manifest declares `SEND_SMS` permission
- All tests pass
