# E06-S03: Data Visualizations (Phase Currents, Grid Reliability, Faults)

**Epic:** 06 — Dashboard  
**Size:** M (2-4h)  
**Dependencies:** E06-S01

## Description
Implement the three core data visualization charts using a Compose charting library (Vico or similar): phase currents multi-line, grid uptime stacked bar, fault distribution donut/pie.

## Acceptance Criteria
- [ ] AC1: Tab bar with 5 tabs: Phase Currents, Grid, Faults, Worker Log, Power AI
- [ ] AC2: Phase Currents: 3-line chart (R=red, Y=amber, B=blue) over 24h
- [ ] AC3: Grid Reliability: Stacked bar chart (uptime=green, downtime=red) by day of week
- [ ] AC4: Fault Distribution: Donut/pie chart with 4 segments (DryRun, Overload, PhaseFail, LowVoltage)
- [ ] AC5: Tab switching animates chart transition
- [ ] AC6: Charts handle empty data gracefully (show "No data yet" message)
- [ ] AC7: Touch/tap on chart elements shows tooltip with values
- [ ] AC8: Charts resize correctly in different orientations

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/
├── ChartTabBar.kt
├── PhaseCurrentChart.kt
├── GridReliabilityChart.kt
└── FaultDistributionChart.kt

app/src/androidTest/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/ChartTabBarTest.kt
```

## Test Requirements
- Tab bar renders 5 tabs, first selected by default
- Tap "Grid" tab → grid chart displayed
- Empty data → "No data yet" message visible
- Chart composables don't crash with empty lists
