# E07-S01: Prediction Engine Core — Power, Fault, Worker Algorithms

**Epic:** 07 — Prediction Engine  
**Size:** L (4-8h)  
**Dependencies:** E02-S02

## Description
The core prediction engine that analyzes 14 days of historical data to predict: (1) next power failure time, (2) next fault type/time, (3) worker's next ON time, and (4) forgot-to-OFF probability. Pure Kotlin algorithms — no AI model dependency.

## Acceptance Criteria
- [ ] AC1: `PredictionEngine` class with 4 prediction methods, each returning a typed result
- [ ] AC2: `predictPowerFailure()` analyzes hourly voltage averages → returns predicted time + confidence
- [ ] AC3: `predictNextFault()` analyzes fault frequency + phase current drift → returns risk timeline
- [ ] AC4: `predictWorkerNextOn()` computes mean/std of ON times with day-of-week grouping → predicted time
- [ ] AC5: `predictForgotOff()` computes risk % based on day-of-week, historical forgot rate, current runtime
- [ ] AC6: All predictions require minimum 3 days of data — return `InsufficientData` otherwise
- [ ] AC7: Results cached in `PredictionCacheEntity` with 1-hour validity
- [ ] AC8: `PredictionScheduler` re-computes on every new telemetry SMS arrival
- [ ] AC9: All 4 algorithms have unit tests with deterministic mock data (no randomness)
- [ ] AC10: Edge cases: empty data, single day data, all-fault data — no crashes

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/prediction/
├── PredictionEngine.kt
├── algorithm/
│   ├── PowerFailurePredictor.kt
│   ├── FaultPredictor.kt
│   ├── WorkerOnTimePredictor.kt
│   └── ForgotOffPredictor.kt
├── model/
│   ├── PowerFailurePrediction.kt
│   ├── FaultPrediction.kt
│   ├── WorkerOnPrediction.kt
│   ├── ForgotOffPrediction.kt
│   └── RiskTimelineEntry.kt
├── PredictionScheduler.kt
└── domain/
    ├── GetPowerFailurePredictionUseCase.kt
    ├── GetFaultPredictionUseCase.kt
    ├── GetWorkerOnPredictionUseCase.kt
    └── GetForgotOffPredictionUseCase.kt

app/src/test/java/com/ksetrasevakah/feature/pumpiq/prediction/algorithm/
├── PowerFailurePredictorTest.kt
├── FaultPredictorTest.kt
├── WorkerOnTimePredictorTest.kt
└── ForgotOffPredictorTest.kt
app/src/test/java/com/ksetrasevakah/feature/pumpiq/prediction/PredictionEngineTest.kt
```

## Implementation Details

### PowerFailurePredictor Algorithm
```kotlin
/**
 * 1. Query telemetry_log for last 14 days of voltage readings
 * 2. Group by hour-of-day → compute hourly average voltage
 * 3. Find hours where avg voltage < 210V (warning) or < 200V (danger)
 * 4. The earliest danger-hour today that hasn't passed yet = predicted failure time
 * 5. Confidence = (days_with_dip_at_that_hour / 14) * 100
 */
data class PowerFailurePrediction(
    val predictedTime: LocalTime?,       // null if no failure expected
    val predictedVoltage: Float?,
    val confidence: Float,               // 0.0–1.0
    val riskLevel: RiskLevel,
    val safeWindow: Pair<LocalTime, LocalTime>?,  // e.g., 06:00–16:00
    val historicalDipCount: Int,          // out of 14 days
)
```

### FaultPredictor Algorithm
```kotlin
/**
 * 1. Query fault_log for last 14 days
 * 2. Compute fault frequency per hour-of-day
 * 3. Compute current phase drift (trend in R/Y/B over last 6h)
 * 4. Build risk timeline: for each +2h window from now to +12h, compute:
 *    - Base risk = (historical_faults_at_that_hour / total_observations)
 *    - Drift bonus = if B-phase trending > 4.2A, add 20%
 *    - Runtime bonus = longer runtime increases dry-run risk
 * 5. Most likely fault type = most frequent in historical window
 */
data class FaultPrediction(
    val nextFaultHours: Float?,           // hours from now
    val mostLikelyType: String?,          // DRY_RUN, OVERLOAD, etc.
    val riskTimeline: List<RiskTimelineEntry>,
    val riskLevel: RiskLevel,
    val confidence: Float,
)

data class RiskTimelineEntry(
    val hoursFromNow: Float,
    val riskPercent: Int,                 // 0–100
    val riskLevel: RiskLevel,
)
```

### WorkerOnTimePredictor Algorithm
```kotlin
/**
 * 1. Query worker_activity for last 14 days
 * 2. Group on_time by day-of-week
 * 3. Compute mean + std for target day-of-week
 * 4. If < 3 data points for that day, use overall average
 * 5. Confidence = 1.0 - (std / mean) capped at 0.95
 */
data class WorkerOnPrediction(
    val predictedTime: LocalTime,
    val confidence: Float,
    val earliestHistorical: LocalTime,
    val latestHistorical: LocalTime,
    val standardDeviationMinutes: Int,
    val dayOfWeekAverage: LocalTime,
)
```

### ForgotOffPredictor Algorithm
```kotlin
/**
 * 1. Query worker_activity for last 14 days
 * 2. Compute forgot_off rate = forgot_count / total_days
 * 3. Adjust by day-of-week factor:
 *    - Weekend/Friday: +0.15
 *    - Wednesday: baseline
 * 4. If motor currently ON and past avg_off_time + 30min → risk doubles
 * 5. Cap at 95%
 */
data class ForgotOffPrediction(
    val riskPercent: Int,                 // 0–100
    val riskLevel: RiskLevel,
    val normalShutdownWindow: Pair<LocalTime, LocalTime>,
    val historicalForgotCount: Int,
    val totalDays: Int,
    val watchdogAlertTime: LocalTime?,    // avg_off + 45min
)
```

## Test Requirements

### PowerFailurePredictorTest
- 14 days with consistent 19:00 dip below 200V → predicts ~19:00, confidence ≥ 0.7
- 14 days with no dips → predictedTime = null, riskLevel = LOW
- 5 days with dips, 9 without → confidence ~0.36
- Only 2 days of data → returns InsufficientData
- All voltages exactly 200V → borderline handling

### FaultPredictorTest
- 12 dry runs at 14:00 window over 14 days → high risk at +N hours from now to 14:00
- No faults in history → flat low-risk timeline
- B-phase trending at 4.5A → risk boosted by drift factor
- Mixed fault types → mostLikelyType = most frequent

### WorkerOnTimePredictorTest
- Consistent 06:15 ON times → predicts 06:15, confidence > 0.9
- High variance ON times (05:30–07:30) → lower confidence
- Wednesday-specific average differs from overall → uses day-specific
- Only 1 data point → uses overall average, lower confidence

### ForgotOffPredictorTest
- 2 forgot events in 14 days → riskPercent ≈ 14%
- 0 forgot events → riskPercent low (~5% baseline)
- Friday + historical forgots → elevated risk
- Motor ON past avg off + 45min → risk doubled
- Empty worker history → InsufficientData

### PredictionEngineTest
- All 4 predictions run without error on valid data
- Cache stores results → second call within validity returns cached
- New telemetry arrival → triggers re-computation

## Definition of Done
- All 4 algorithms are deterministic given same input
- All edge cases handled (empty data, insufficient data, boundary values)
- Cached results have 1-hour TTL
- 20+ unit tests pass across all predictor classes
