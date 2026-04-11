# WIRE-04: Fix Motor State Reactivity — SMS Events Drive Dashboard Status

**Severity:** 🔴 CRITICAL  
**Size:** M (2-4h)  
**Dependencies:** WIRE-03

## Problem
Dashboard shows motor as "Offline" even when the farmer turns it ON via Keypad/APP, because:
1. `MotorStateMachine` only processes states from SMS commands WE send (START/STOP), not from external actions
2. When farmer turns motor ON via Keypad, SMS says "Motor Turned ON via : Keypad" — this is NOT a response to our SMS command, it's an unsolicited notification
3. The dashboard ViewModel observes `MotorStateRepository` but the state never changes

## Root Cause
The current design assumes motor is ONLY controlled via our app. In reality, the farmer uses the physical Keypad or Taro APP to control the motor. Our app is a **passive observer** of motor state, not the sole controller.

## Acceptance Criteria
- [ ] AC1: When SMS "Motor Turned ON via : Keypad" arrives → motor state updates to ON immediately
- [ ] AC2: When SMS "Motor Turned OFF via : Keypad" arrives → motor state updates to OFF immediately
- [ ] AC3: When SMS "Motor Turned ON via : APP" arrives → motor state updates to ON
- [ ] AC4: When SMS "Device Powered ON / Motor Status :Off" arrives → motor state confirms OFF
- [ ] AC5: Dashboard observes motor state Flow → UI updates within 1 second of SMS arrival
- [ ] AC6: Session timer starts counting from the MotorOn event timestamp (not current time)
- [ ] AC7: Session timer stops when MotorOff event arrives
- [ ] AC8: Motor state survives app restart (persisted in Room)
- [ ] AC9: If motor is ON and PowerFailure arrives → motor state stays ON (motor trips, but we don't know until next SMS confirms OFF)
- [ ] AC10: The START/STOP buttons still work for sending SMS commands — but motor state is ALWAYS driven by incoming SMS, not by our outbound command

## Key Design Change
```
BEFORE: App sends START → PENDING → waits for "MOTOR ON" → ON
AFTER:  Motor state is ALWAYS set by incoming SMS, regardless of source:
        - "Motor Turned ON via : Keypad" → ON
        - "Motor Turned ON via : APP" → ON
        - Our START command → PENDING → incoming "Motor Turned ON" → ON
        - Physical keypad → incoming "Motor Turned ON via : Keypad" → ON (no PENDING)
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/sms/MotorStateMachine.kt
app/src/main/java/com/ksetrasevakah/core/ai/SmsTelemetryProcessor.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/DashboardViewModel.kt
```

## Test Requirements
- External MotorOn (Keypad) → state jumps OFF→ON (no PENDING intermediate)
- Our START command → PENDING → external MotorOn → ON (resolves pending)
- External MotorOff (Keypad) → state ON→OFF
- DevicePoweredOn with motorStatus=Off → confirms OFF state
- PowerFailure → motor state unchanged (we don't know motor tripped yet)
- Dashboard Flow emission tested with Turbine
