# E03-S03: Motor State Machine

**Epic:** 03 — SMS Engine  
**Size:** M (2-4h)  
**Dependencies:** E03-S01, E03-S02

## Description
Implement the motor state machine that manages transitions: OFF → PENDING_START → ON → PENDING_STOP → OFF. Handles timeout (no SMS reply within 30s), confirmation processing, and worker activity logging.

## Acceptance Criteria
- [ ] AC1: `MotorStateMachine` class manages transitions with validation
- [ ] AC2: Valid transitions: OFF→PENDING_START, PENDING_START→ON, ON→PENDING_STOP, PENDING_STOP→OFF, PENDING_*→OFF (timeout)
- [ ] AC3: Invalid transitions throw/return error (e.g., OFF→ON directly)
- [ ] AC4: On transition to ON: `WorkerActivityEntity.onTime` set to now
- [ ] AC5: On transition to OFF: `WorkerActivityEntity.offTime` set to now, duration calculated
- [ ] AC6: PENDING states auto-timeout to OFF after 30 seconds with `Result.Error`
- [ ] AC7: `processConfirmation(smsBody: String)` resolves pending state based on SMS content
- [ ] AC8: State changes propagate via `Flow` to all observers
- [ ] AC9: All state transitions have unit tests

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/sms/
└── MotorStateMachine.kt

app/src/main/java/com/ksetrasevakah/feature/pumpiq/domain/usecase/
└── ProcessSmsConfirmationUseCase.kt

app/src/test/java/com/ksetrasevakah/core/sms/MotorStateMachineTest.kt
app/src/test/java/com/ksetrasevakah/feature/pumpiq/domain/usecase/ProcessSmsConfirmationUseCaseTest.kt
```

## Implementation Details

### State Transition Table
```
Current State     | Event              | Next State      | Side Effects
OFF               | SendStart          | PENDING_START   | Set pendingCommand, pendingSince
PENDING_START     | ConfirmOn          | ON              | Set currentSessionStart, worker.onTime
PENDING_START     | Timeout(30s)       | OFF             | Clear pending, log timeout error
PENDING_START     | ConfirmError       | OFF             | Clear pending, log fault
ON                | SendStop           | PENDING_STOP    | Set pendingCommand, pendingSince
PENDING_STOP      | ConfirmOff         | OFF             | Set worker.offTime, calc duration
PENDING_STOP      | Timeout(30s)       | OFF             | Clear pending, log timeout error
```

### SMS Confirmation Parsing
```kotlin
// "MOTOR ON. R=3.82A Y=3.71A B=3.89A V=228V" → ConfirmOn
// "MOTOR OFF. RUNTIME=4H22M. NO FAULTS." → ConfirmOff
// "ALERT: DRY RUN DETECTED. MOTOR STOPPED." → ConfirmError + FaultEntity
```

## Test Requirements

### MotorStateMachineTest
- OFF + SendStart → PENDING_START
- PENDING_START + ConfirmOn → ON
- ON + SendStop → PENDING_STOP
- PENDING_STOP + ConfirmOff → OFF
- PENDING_START + Timeout → OFF
- OFF + SendStop → Error("Motor already off")
- ON + SendStart → Error("Motor already running")
- Verify worker activity created on ON transition
- Verify worker activity completed on OFF transition

### ProcessSmsConfirmationUseCaseTest
- "MOTOR ON..." → motor state becomes ON
- "MOTOR OFF..." → motor state becomes OFF
- "ALERT: DRY RUN..." → motor OFF + fault logged

## Definition of Done
- State machine prevents invalid transitions
- Worker activity auto-logged
- Timeout handling works
- All tests pass
