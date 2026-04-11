# WIRE-06: Fix Dashboard Data Binding — Charts, Summary, Prediction Cards

**Severity:** 🔴 CRITICAL  
**Size:** L (4-8h)  
**Dependencies:** WIRE-04, WIRE-05

## Problem
Dashboard is empty because:
1. Charts query `TelemetryRepository` which has old schema — needs to query `PanelEventDao`
2. Phase Current chart expects continuous R/Y/B amperage — Taro only sends voltage (and only during alerts)
3. Grid Reliability chart expects uptime/downtime hours — needs to compute from PowerFailure/PowerResumed events
4. Fault Distribution chart expects fault counts — needs to query FaultEntity correctly
5. AI Daily Summary is hardcoded placeholder text
6. Prediction cards show default "No data" values

## Acceptance Criteria

### Charts
- [ ] AC1: **Voltage Chart** (replaces "Phase Currents"): Plots R/Y/B voltage readings from LowVoltage + PhaseFailure events over time. X=time, Y=voltage (340-400V range for 3-phase)
- [ ] AC2: **Grid Reliability Chart**: Computed from PowerFailure/PowerResumed pairs. Each day shows hours of uptime vs downtime. Uses PanelEventDao.getPowerEvents()
- [ ] AC3: **Fault Distribution Chart**: Pie chart of DRY_RUN, PHASE_FAILURE, LOW_VOLTAGE, OVERLOAD counts from FaultEntity. Uses FaultDao.getFaultDistribution()
- [ ] AC4: **Worker Pattern Chart**: ON/OFF timeline bars from WorkerActivityEntity. Each day = one row with ON→OFF bar. Forgot-OFF highlighted
- [ ] AC5: **Power Event Timeline** (replaces "Power AI"): Scatter/timeline of PowerFailure and PowerResumed events. Shows pattern of grid instability

### Summary Card
- [ ] AC6: AI Daily Summary generated from real data: "Today: Motor ON at 08:43 via Keypad. 2 power failures. 1 low voltage alert at 349V. Motor running 3h 12m."
- [ ] AC7: Summary updates when new events arrive

### Prediction Cards
- [ ] AC8: Power Failure card shows predicted next failure time from WIRE-05
- [ ] AC9: Next Fault card shows predicted fault type and time
- [ ] AC10: Tomorrow ON card shows predicted worker start time
- [ ] AC11: Forgot OFF card shows current risk percentage
- [ ] AC12: Cards tappable → navigate to chat with pre-filled query

## Key Schema Change for Charts
```
OLD: TelemetryRepository.getRecent() → continuous amperage readings (doesn't exist)
NEW: PanelEventDao queries → discrete event-driven data points

Charts must handle SPARSE data — voltage readings only exist during alert events,
not continuously. The chart should plot available points and connect them,
or show a "No continuous monitoring" note.
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/DashboardViewModel.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/DashboardScreen.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/PhaseCurrentChart.kt → VoltageChart.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/GridReliabilityChart.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/FaultDistributionChart.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/WorkerPatternChart.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/PowerForecastChart.kt → PowerEventTimeline.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/PredictionCardsSection.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/model/DashboardUiState.kt
```

## Test Requirements
- ViewModel loads voltage data from PanelEventDao → UiState.voltageData populated
- ViewModel computes grid uptime from power events → UiState.gridData populated  
- ViewModel loads fault distribution → UiState.faultData populated
- Empty data → charts show "No data yet" message
- Prediction cards show computed values, not defaults
