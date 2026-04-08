# E07-S04: Power Forecast Chart with AI Voltage Prediction

**Epic:** 07 — Prediction Engine  
**Size:** M (2-4h)  
**Dependencies:** E07-S01, E06-S03

## Description
The "Power AI" chart tab showing actual voltage readings (solid line) and predicted future voltage (dashed line), with a 200V danger reference line.

## Acceptance Criteria
- [ ] AC1: Chart displays actual voltage as solid green line (past hours)
- [ ] AC2: Chart displays predicted voltage as dashed amber line (future hours)
- [ ] AC3: 200V danger threshold shown as red dashed horizontal reference line labeled "DANGER 200V"
- [ ] AC4: Predicted danger zone (below 200V) shown as red dashed line
- [ ] AC5: X-axis shows time in HH:MM, Y-axis shows voltage 180V–245V
- [ ] AC6: Tooltip on touch shows exact voltage at that time point
- [ ] AC7: Chart label: "Voltage + AI forecast (dashed = predicted)"
- [ ] AC8: Handles case where no prediction available (show only actual data)

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/
└── PowerForecastChart.kt

app/src/androidTest/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/PowerForecastChartTest.kt
```

## Test Requirements
- Chart renders with actual + predicted lines
- Danger reference line visible at y=200
- Empty prediction data → only actual line shown
- Chart composable doesn't crash with empty data list
