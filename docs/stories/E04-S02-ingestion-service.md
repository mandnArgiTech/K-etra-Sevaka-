# E04-S02: Ingestion Foreground Service & SMS Parsing

**Epic:** 04 — AI Ingestion Layer  
**Size:** L (4-8h)  
**Dependencies:** E04-S01, E03-S02

## Description
Create the foreground service that receives SMS from the BroadcastReceiver and uses the 0.5B model to extract structured data (phase currents, voltage, faults, motor state) into Room database entities.

## Acceptance Criteria
- [ ] AC1: `IngestionService` is an Android Foreground Service with persistent notification
- [ ] AC2: Receives SMS body via Intent extras from `SmsReceiver`
- [ ] AC3: Feeds SMS text to 0.5B model with structured extraction prompt
- [ ] AC4: Parses 0.5B model JSON output into `TelemetryEntity` fields
- [ ] AC5: Saves `TelemetryEntity` to Room
- [ ] AC6: Detects fault keywords → creates `FaultEntity`
- [ ] AC7: Updates `MotorStateEntity` based on extracted state
- [ ] AC8: Updates `WorkerActivityEntity` for ON/OFF transitions
- [ ] AC9: Handles malformed SMS gracefully (logs error, doesn't crash)
- [ ] AC10: Service starts on app launch, survives app backgrounding

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/ai/
├── IngestionService.kt               # Foreground service
├── prompt/IngestionPrompt.kt          # System prompt for 0.5B extraction
└── parser/TelemetryParser.kt          # Parse 0.5B JSON output → entities

app/src/test/java/com/ksetrasevakah/core/ai/parser/TelemetryParserTest.kt
app/src/test/java/com/ksetrasevakah/core/ai/IngestionServiceTest.kt
```

## Implementation Details

### 0.5B Extraction Prompt
```
You are a telemetry data extractor for a Taro Smart Panel motor controller.
Given an SMS message, extract data into this exact JSON format:
{
  "motor_on": true/false,
  "phase_r": float or null,
  "phase_y": float or null,
  "phase_b": float or null,
  "voltage": float or null,
  "temperature": float or null,
  "runtime_minutes": int or null,
  "fault_type": "DRY_RUN"|"OVERLOAD"|"PHASE_FAIL"|"LOW_VOLTAGE"|null,
  "fault_description": string or null
}
Only output valid JSON. No other text.
```

### SMS Examples → Expected Extraction
```
"MOTOR ON. R=3.82A Y=3.71A B=3.89A V=228V TEMP=42C"
→ { motor_on: true, phase_r: 3.82, phase_y: 3.71, phase_b: 3.89, voltage: 228, temperature: 42 }

"ALERT: DRY RUN DETECTED. MOTOR STOPPED."
→ { motor_on: false, fault_type: "DRY_RUN", fault_description: "Dry run detected, motor auto-stopped" }

"STATUS: MOTOR ON. UPTIME=6H14M. R=3.80A Y=3.68A B=3.91A V=225V"
→ { motor_on: true, phase_r: 3.80, phase_y: 3.68, phase_b: 3.91, voltage: 225, runtime_minutes: 374 }
```

## Test Requirements

### TelemetryParserTest
- Valid JSON → correct TelemetryEntity fields
- Missing optional fields → null values (no crash)
- Malformed JSON → Result.Error with message
- Fault detection → FaultEntity created with correct type
- Runtime "6H14M" → 374 minutes conversion

### IngestionServiceTest (Robolectric)
- Service starts with valid Intent → processes SMS
- Service starts with empty body → logs warning, no crash
- Multiple SMS in quick succession → all processed sequentially

## Definition of Done
- Service registered in AndroidManifest with FOREGROUND_SERVICE permission
- Notification channel created for foreground service
- All SMS from Taro panel get parsed and stored
- All tests pass
