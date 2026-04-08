# E07-S03: Worker Pattern Chart & Forgot-OFF Watchdog

**Epic:** 07 — Prediction Engine  
**Size:** M (2-4h)  
**Dependencies:** E07-S01, E06-S03

## Description
The "Worker Log" chart tab showing 14 days of ON/OFF times as horizontal timeline bars, with FORGOT flags highlighted in red. Plus the background watchdog that alerts when motor runs past expected off-time.

## Acceptance Criteria
- [ ] AC1: Worker Log chart tab shows 14 rows, one per day
- [ ] AC2: Each row: date | ON time | horizontal bar (proportional to duration) | OFF time
- [ ] AC3: Forgot-OFF days highlighted with red gradient bar and "FORGOT" label
- [ ] AC4: Today's row shows "TODAY" label and no OFF time if motor still running
- [ ] AC5: `ForgotOffWatchdog` background coroutine monitors motor state
- [ ] AC6: If motor ON past (avg_off_time + 45min) → triggers notification
- [ ] AC7: If motor ON past (avg_off_time + 75min) → shows "AUTO-STOP RECOMMENDED"
- [ ] AC8: If motor ON past (avg_off_time + 105min) → auto-sends STATUS SMS
- [ ] AC9: Watchdog respects user settings (can be enabled/disabled)
- [ ] AC10: Unit tests for watchdog threshold logic

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/
└── WorkerPatternChart.kt

app/src/main/java/com/ksetrasevakah/feature/pumpiq/prediction/
└── ForgotOffWatchdog.kt

app/src/test/java/com/ksetrasevakah/feature/pumpiq/prediction/ForgotOffWatchdogTest.kt
app/src/androidTest/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/WorkerPatternChartTest.kt
```

## Test Requirements
### ForgotOffWatchdogTest
- Motor ON at 19:30 (avg off = 18:45) → 45min threshold: alert at 19:30 ✓
- Motor OFF before threshold → no alert
- Motor ON past 75min threshold → "AUTO-STOP RECOMMENDED" flag
- Motor ON past 105min threshold → STATUS SMS triggered
- Watchdog disabled in settings → no alerts

### WorkerPatternChartTest
- 14 rows rendered
- Forgot-OFF day shows red highlight and "FORGOT" text
- Today row shows "TODAY" label
