# FIX-13: Return Result.Error for Invalid Motor State Transitions

**Severity:** 🟢 MINOR  
**Size:** S (< 1h)  
**Dependencies:** None

## Problem
`MotorStateMachine.sendStart()` silently returns the current state for invalid transitions (e.g., calling START when motor is already ON). The farmer gets no feedback — they tap START, nothing happens, no error.

## Acceptance Criteria
- [ ] AC1: `sendStart()` returns `Result<MotorState>` instead of `MotorState`
- [ ] AC2: `sendStart()` when ON → `Result.Error("Motor is already running")`
- [ ] AC3: `sendStart()` when PENDING → `Result.Error("Command already in progress")`
- [ ] AC4: `sendStop()` when OFF → `Result.Error("Motor is already off")`
- [ ] AC5: `sendStop()` when PENDING → `Result.Error("Command already in progress")`
- [ ] AC6: Valid transitions still return `Result.Success(newState)`
- [ ] AC7: ViewModel shows error message via Snackbar/Toast on `Result.Error`
- [ ] AC8: Existing tests updated to assert `Result` types

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/sms/MotorStateMachine.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/DashboardViewModel.kt
app/src/test/java/com/ksetrasevakah/core/sms/MotorStateMachineTest.kt
```
