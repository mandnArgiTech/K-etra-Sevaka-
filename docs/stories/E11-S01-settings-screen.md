# E11-S01: Settings Screen

**Epic:** 11 — Integration & Polish  
**Size:** M (2-4h)  
**Dependencies:** E10-S01, E05-S01

## Description
Settings screen with sections: Taro Panel config, AI model info, backup controls, watchdog toggle.

## Acceptance Criteria
- [ ] AC1: Panel section: shows target number "070936 52065" (read-only display)
- [ ] AC2: AI section: shows loaded model IDs and memory usage
- [ ] AC3: Backup section: last backup time, "Backup Now" button, "Restore" button
- [ ] AC4: Watchdog section: enable/disable forgot-OFF watchdog toggle
- [ ] AC5: Watchdog threshold configurable (default 45 min)
- [ ] AC6: About section: app version, device info
- [ ] AC7: "Backup Now" triggers manual backup with progress
- [ ] AC8: "Restore" opens backup list picker

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/settings/
├── SettingsScreen.kt
└── SettingsViewModel.kt

app/src/test/java/com/ksetrasevakah/feature/settings/SettingsViewModelTest.kt
app/src/androidTest/java/com/ksetrasevakah/feature/settings/SettingsScreenTest.kt
```

## Test Requirements
- Panel number displayed correctly
- Watchdog toggle changes persisted
- Backup Now button triggers BackupManager
