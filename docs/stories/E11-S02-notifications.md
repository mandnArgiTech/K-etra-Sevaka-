# E11-S02: Notifications (Forgot-OFF Watchdog, Fault Alerts)

**Epic:** 11 — Integration & Polish  
**Size:** M (2-4h)  
**Dependencies:** E07-S03, E04-S02

## Description
Push notifications for critical events: forgot-OFF watchdog alerts, fault detection alerts, and SMS timeout warnings.

## Acceptance Criteria
- [ ] AC1: Notification channels created: "Motor Alerts" (high), "Watchdog" (high), "System" (default)
- [ ] AC2: Forgot-OFF watchdog → notification at 45min/75min/105min thresholds
- [ ] AC3: Fault detected (dry run, overload, etc.) → immediate notification
- [ ] AC4: SMS send timeout (30s no reply) → notification "No response from panel"
- [ ] AC5: Tapping notification → opens relevant screen (dashboard for motor, chat for faults)
- [ ] AC6: Notifications respect Do Not Disturb settings
- [ ] AC7: Notification content includes actionable info (e.g., "Motor still running — tap to stop")

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/notification/
├── NotificationManager.kt
├── NotificationChannels.kt
└── di/NotificationModule.kt

app/src/test/java/com/ksetrasevakah/core/notification/NotificationManagerTest.kt
```

## Test Requirements
- Watchdog threshold reached → notification built with correct content
- Fault detected → notification with fault type
- Notification tap intent targets correct screen
