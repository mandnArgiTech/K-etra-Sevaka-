# E05-S02: Hub Screen (Module Launcher)

**Epic:** 05 — UI Shell  
**Size:** S (1-2h)  
**Dependencies:** E05-S01, E01-S02

## Description
Build the Kṣetra Sevakaḥ main menu — modular entry point showing PumpIQ (active) and future modules (CropDoctor, SoilAnalytics) as coming soon. Includes settings & backup access.

## Acceptance Criteria
- [ ] AC1: Hub screen renders app title "Kṣetra Sevakaḥ" with Playfair Display font
- [ ] AC2: PumpIQ module card is tappable → navigates to Dashboard
- [ ] AC3: CropDoctor and SoilAnalytics cards show "Coming Soon" with reduced opacity
- [ ] AC4: Coming-soon cards are NOT tappable
- [ ] AC5: Settings and Backup cards render at bottom
- [ ] AC6: Device info footer shows "XIAOMI 14 CIVI • SNAPDRAGON 8s GEN 3 • MLC-LLM"
- [ ] AC7: UI test verifies PumpIQ tap navigates to Dashboard

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/hub/
├── HubScreen.kt
└── HubViewModel.kt

app/src/androidTest/java/com/ksetrasevakah/feature/hub/HubScreenTest.kt
```

## Test Requirements
- PumpIQ card visible with "Active" badge
- Tap PumpIQ → navigation event dispatched
- CropDoctor card visible with "Soon" badge, alpha reduced
- CropDoctor tap → no navigation event
