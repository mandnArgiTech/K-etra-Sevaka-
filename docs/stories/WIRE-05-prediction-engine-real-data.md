# WIRE-05: Wire Prediction Engine to Real Parsed Data

**Severity:** 🔴 CRITICAL  
**Size:** L (4-8h)  
**Dependencies:** WIRE-02, WIRE-03

## Problem
All 4 prediction algorithms expect data that doesn't exist in the current schema:
- `PowerFailurePredictor` reads voltage from `TelemetryEntity` — but real voltage data is in LowVoltage/PhaseFailure events, not in every SMS
- `FaultPredictor` reads phase current trends — but Taro only sends current during Dryrun/Overload faults, not continuously
- `WorkerOnTimePredictor` reads `worker_activity` ON times — partially works IF WIRE-03 populates it
- `ForgotOffPredictor` same dependency on worker_activity

The predictions need to use the REAL event patterns from the Taro panel.

## Acceptance Criteria
- [ ] AC1: `PowerFailurePredictor` queries `PanelEventDao` for POWER_FAILURE events → predicts based on historical failure times (hour-of-day clustering)
- [ ] AC2: Power prediction also uses LOW_VOLTAGE events as precursors — low voltage at 7AM often precedes failure at 8AM
- [ ] AC3: `FaultPredictor` queries `FaultEntity` by type → predicts based on fault frequency and time patterns
- [ ] AC4: Fault prediction uses DRYRUN time clustering (from real data: 14:32 is a dryrun hotspot)
- [ ] AC5: `WorkerOnTimePredictor` queries `PanelEventDao` for MOTOR_ON events → extracts hour+minute → computes average start time per day-of-week
- [ ] AC6: `ForgotOffPredictor` looks at MOTOR_ON without matching MOTOR_OFF within 14 hours → flags forgot-off risk
- [ ] AC7: All predictors handle insufficient data (< 3 days) → return "Insufficient data" result
- [ ] AC8: Predictions update when new SMS arrives (triggered by SmsTelemetryProcessor)
- [ ] AC9: Dashboard prediction cards show real computed values, not placeholder text
- [ ] AC10: All 4 predictors have unit tests with seeded real-format data

## Real Data Patterns (from 10-day analysis)

**Power Failures:** 34 events. Heavy clustering at night (01:00-03:00) and morning (07:00-09:00). Pattern: power unstable during peak evening/early morning grid load.

**Motor ON times:** Mostly 07:00-11:00 via Keypad. Worker typically starts between 07:27 and 10:59.

**Motor OFF times:** Scattered, some very late (forgot-off risk): 05:51 OFF after ON at 07:12 previous day.

**Dryruns:** 3 events, all at 14:32 — strong afternoon clustering.

**Low Voltage:** 24 events, voltages around 344-352V (this is a 3-phase 415V system, so these are per-phase voltages, not the 220V we assumed).

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/prediction/algorithm/PowerFailurePredictor.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/prediction/algorithm/FaultPredictor.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/prediction/algorithm/WorkerOnTimePredictor.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/prediction/algorithm/ForgotOffPredictor.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/prediction/PredictionEngine.kt
```

## Test Requirements
- Seed 10 days of PanelEvents → PowerFailurePredictor predicts morning/night window
- Seed 3 Dryrun events at 14:xx → FaultPredictor flags afternoon dryrun risk
- Seed motor ON events at 07:30-08:30 → WorkerOnTimePredictor predicts ~08:00
- Seed motor ON without OFF for 16h → ForgotOffPredictor flags HIGH risk
- < 3 days of data → all predictors return InsufficientData
