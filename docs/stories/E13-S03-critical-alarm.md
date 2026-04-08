# E13-S03: Critical Alarm System — DND Override, Sound, Overlay

**Epic:** 13 — Surakṣā Notification Engine  
**Size:** M (2-4h)  
**Dependencies:** E13-S02

## Description
When CRITICAL threat detected (tampering or coordinated intrusion): override Do Not Disturb, play loud alarm, vibrate aggressively, push full-screen high-priority notification, and emit state for the red critical overlay UI.

## Acceptance Criteria
- [ ] AC1: `CriticalAlarmManager.triggerCriticalAlarm(event)` executes full alarm sequence
- [ ] AC2: Requests DND override via `NOTIFICATION_POLICY_ACCESS` permission
- [ ] AC3: Sets alarm stream to max volume, plays `R.raw.critical_alarm` (15-second alert)
- [ ] AC4: Vibration pattern: urgent bursts (500ms on, 200ms off, 500ms on, 200ms off, 1000ms on)
- [ ] AC5: Pushes notification with `CATEGORY_ALARM`, `PRIORITY_MAX`, full-screen intent
- [ ] AC6: Full-screen intent opens MainActivity with `critical_event_id` extra
- [ ] AC7: `CriticalOverlayState` (SharedFlow) emits `CriticalOverlayData` observed by UI
- [ ] AC8: `dismissCriticalAlarm()` stops sound, vibration, clears overlay state
- [ ] AC9: Alarm auto-dismisses after 30 seconds if user doesn't interact
- [ ] AC10: Handles missing NOTIFICATION_POLICY_ACCESS gracefully (alarm still works, just can't override DND)

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/notification/
├── CriticalAlarmManager.kt
└── model/CriticalOverlayData.kt

app/src/main/res/raw/
└── critical_alarm.ogg                   # 15-second alert tone

app/src/test/java/com/ksetrasevakah/core/notification/CriticalAlarmManagerTest.kt
```

## Test Requirements
- triggerCriticalAlarm → MediaPlayer started, vibrator activated
- triggerCriticalAlarm → notification built with PRIORITY_MAX
- dismissCriticalAlarm → MediaPlayer stopped, overlay cleared
- Auto-dismiss after 30s timeout
- Missing DND permission → alarm still plays, no crash
