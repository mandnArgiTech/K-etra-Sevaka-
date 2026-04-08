# E11-S03: End-to-End Integration Test Suite

**Epic:** 11 — Integration & Polish  
**Size:** L (4-8h)  
**Dependencies:** All previous stories

## Description
Comprehensive integration tests covering the full user flows: START pump → SMS confirm → dashboard updates → AI chat query → prediction display.

## Acceptance Criteria
- [ ] AC1: E2E test: User taps START → SMS sent → mock reply received → UI shows RUNNING + timer
- [ ] AC2: E2E test: RUNNING state → phase currents displayed → user taps STOP → UI shows OFFLINE
- [ ] AC3: E2E test: Prediction cards show computed values from seeded 14-day data
- [ ] AC4: E2E test: Tap prediction card → navigates to chat → query auto-sent → AI responds
- [ ] AC5: E2E test: Chat thread created → messages persisted → thread appears in drawer
- [ ] AC6: E2E test: Forgot-OFF watchdog fires notification after threshold
- [ ] AC7: E2E test: Navigation flow Hub → Dashboard → Chat → back → back → Hub
- [ ] AC8: E2E test: Worker activity log updated on motor ON/OFF transitions
- [ ] AC9: All integration tests use in-memory Room DB + mocked SMS/AI
- [ ] AC10: Code coverage report generated — target 95% core, 85% UI

## Files to Create
```
app/src/androidTest/java/com/ksetrasevakah/integration/
├── MotorControlFlowTest.kt
├── PredictionFlowTest.kt
├── ChatFlowTest.kt
├── NavigationFlowTest.kt
├── WatchdogFlowTest.kt
└── WorkerActivityFlowTest.kt

app/src/androidTest/java/com/ksetrasevakah/integration/util/
├── TestDataSeeder.kt                  # Seeds 14 days of mock data
└── FakeSmsManager.kt                  # Mock SMS send/receive
```

## Test Data Seeder
Must seed Room DB with:
- 14 days of worker_activity (2 with forgot_off = true)
- 14 days of telemetry_log (hourly voltage, phase currents)
- 28 fault_log entries (12 dry run, 8 overload, 5 phase fail, 3 low voltage)
- Motor state = OFF

## Definition of Done
- All 6 integration test files pass
- Coverage report shows ≥ 95% on core/, domain/ 
- Coverage report shows ≥ 85% on ui/ (composables)
- No flaky tests (run 3x consistently)
