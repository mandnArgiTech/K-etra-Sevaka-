# E18-S01: Surakṣā End-to-End Integration Tests

**Epic:** 18 — Surakṣā Testing  
**Size:** L (4-8h)  
**Dependencies:** All E12-E17 stories

## Description
Comprehensive integration test suite for Surakṣā: notification interception → threat classification → DB storage → dashboard display → cross-module correlation → chat query.

## Acceptance Criteria
- [ ] AC1: E2E: Simulated Tapo notification → parsed → classified as LOW → saved to DB → notification cancelled → appears in ledger
- [ ] AC2: E2E: TAMPERING notification → classified CRITICAL → alarm triggered → overlay shown → user acknowledges → overlay dismissed
- [ ] AC3: E2E: 3 person detections same camera in 4 min → spike detected → threat elevated
- [ ] AC4: E2E: Night person + PumpIQ power failure within 5 min → cross-module correlation found
- [ ] AC5: E2E: Camera mode set to DROP → notification deleted, no DB entry, no AI processing
- [ ] AC6: E2E: Camera mode set to SILENT → notification dismissed, DB entry created, no user alert
- [ ] AC7: E2E: Auto-discover new camera on first notification → appears in Camera Matrix
- [ ] AC8: E2E: Nightly briefing generated with correct event counts and correlations
- [ ] AC9: E2E: Chat query "Security briefing" → 3B returns briefing with both modules referenced
- [ ] AC10: E2E: Navigation flow Hub → Surakṣā → Camera Matrix → back → Ledger item → Chat → back → Hub

## Files to Create
```
app/src/androidTest/java/com/ksetrasevakah/integration/suraksha/
├── NotificationToLedgerFlowTest.kt
├── CriticalAlarmFlowTest.kt
├── ActivitySpikeFlowTest.kt
├── CrossModuleCorrelationFlowTest.kt
├── CameraModeFlowTest.kt
├── CameraAutoDiscoveryFlowTest.kt
├── SecurityBriefingFlowTest.kt
├── SurakshaChatFlowTest.kt
└── SurakshaNavigationFlowTest.kt

app/src/androidTest/java/com/ksetrasevakah/integration/suraksha/util/
├── FakeTapoNotification.kt            # Builds mock StatusBarNotification
└── SurakshaTestDataSeeder.kt           # Seeds camera configs + events
```

## Test Data Seeder
```kotlin
object SurakshaTestDataSeeder {
    suspend fun seed(db: KsetraDatabase) {
        // 5 cameras: "Front Gate", "Barn Cam", "West Fence", "Pump House", "Transformer"
        // 50 security events across 3 days:
        //   - 30 PERSON LOW (daytime)
        //   - 10 PERSON MEDIUM (evening)
        //   - 7 PERSON HIGH (nighttime)
        //   - 3 TAMPERING CRITICAL
        // 1 cross-module correlation: PERSON @ Transformer 22:14 + power fail 22:16
    }
}
```

## Definition of Done
- All 9 E2E test flows pass
- Coverage addendum: Surakṣā core ≥ 95%, UI ≥ 85%
- No flaky tests (3 consecutive green runs)
