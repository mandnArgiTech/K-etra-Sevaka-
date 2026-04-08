# E14-S02: Critical Threat Full-Screen Overlay

**Epic:** 14 — Surakṣā UI  
**Size:** M (2-4h)  
**Dependencies:** E13-S03, E14-S01

## Description
When a CRITICAL threat is active (tampering detected), a full-screen red overlay appears on top of any screen. Shows camera name, event type, timestamp, pulsing red background, and ACKNOWLEDGE + VIEW buttons.

## Acceptance Criteria
- [ ] AC1: `CriticalAlertOverlay` composable renders above ALL other content (use Popup or Dialog)
- [ ] AC2: Full-screen semi-transparent dark red background with pulsing glow animation
- [ ] AC3: Center content: large shield icon, "CRITICAL ALERT" title, camera name, event type, timestamp
- [ ] AC4: Pulsing red border animation (breathe effect, 1.5s cycle)
- [ ] AC5: Two buttons: "ACKNOWLEDGE" (dismisses overlay + alarm) and "VIEW DETAILS" (goes to chat)
- [ ] AC6: ACKNOWLEDGE → calls `CriticalAlarmManager.dismissCriticalAlarm()` + `securityEventDao.acknowledge(id)`
- [ ] AC7: VIEW DETAILS → dismisses alarm, navigates to chat with query about the event
- [ ] AC8: Overlay auto-dismisses after 60 seconds (alarm already stops at 30s)
- [ ] AC9: Overlay observes `CriticalOverlayState` SharedFlow — appears/disappears reactively
- [ ] AC10: Multiple CRITICAL events stack — latest shown, previous queued

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/suraksha/dashboard/component/
└── CriticalAlertOverlay.kt

app/src/androidTest/java/com/ksetrasevakah/feature/suraksha/dashboard/component/
└── CriticalAlertOverlayTest.kt
```

## Test Requirements
- Overlay visible when CriticalOverlayState has active data
- Overlay hidden when state cleared
- ACKNOWLEDGE tap → dismiss called, overlay disappears
- Camera name and timestamp displayed correctly
- Pulsing animation active (verify modifier exists)
