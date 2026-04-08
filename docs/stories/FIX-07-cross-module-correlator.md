# FIX-07: Wire CrossModuleCorrelator to PumpIQ Repositories

**Severity:** 🟡 IMPORTANT  
**Size:** M (2-4h)  
**Dependencies:** None

## Problem
`CrossModuleCorrelator` only queries `SecurityEventRepository`. It never touches PumpIQ's `TelemetryRepository` or `FaultRepository`. The entire cross-module correlation feature (security event ↔ power failure, security event ↔ motor state change) is unimplemented. It just clusters security events by time proximity.

## Acceptance Criteria
- [ ] AC1: `CrossModuleCorrelator` constructor injects `TelemetryRepository` and `FaultRepository` alongside `SecurityEventRepository`
- [ ] AC2: `findCorrelations()` queries both security events AND PumpIQ telemetry/faults within the time window
- [ ] AC3: Correlation type "PERSON_NEAR_POWER_FAILURE": person detected at camera within ±5 min of a power outage logged in telemetry
- [ ] AC4: Correlation type "PERSON_NEAR_MOTOR_CHANGE": person detected within ±5 min of unexpected motor start/stop
- [ ] AC5: Correlation type "TAMPERING_PLUS_FAULT": camera tampering within ±5 min of any PumpIQ fault
- [ ] AC6: Each correlation has `description: String` explaining the link (e.g., "Person at Transformer Cam at 22:14 — power failure at 22:16")
- [ ] AC7: Each correlation has `severity: RiskLevel` — tampering+fault = CRITICAL, person+power = HIGH
- [ ] AC8: Correlations returned sorted by severity DESC, then by timestamp DESC
- [ ] AC9: Empty PumpIQ data → returns empty correlations (no crash)
- [ ] AC10: Unit tests with mocked repos cover all 3 correlation types + no-match scenario

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/feature/suraksha/prediction/CrossModuleCorrelator.kt
app/src/test/java/com/ksetrasevakah/feature/suraksha/prediction/CrossModuleCorrelatorTest.kt
```

## Implementation Sketch
```kotlin
class CrossModuleCorrelator @Inject constructor(
    private val securityEventRepository: SecurityEventRepository,
    private val telemetryRepository: TelemetryRepository,      // ← ADD
    private val faultRepository: FaultRepository                // ← ADD
) {
    data class CrossModuleCorrelation(
        val securityEvent: SecurityEvent,
        val pumpIqEventDescription: String,
        val pumpIqTimestamp: Long,
        val timeDeltaMs: Long,
        val correlationType: String,  // PERSON_NEAR_POWER_FAILURE, etc.
        val severity: RiskLevel,
        val description: String
    )
    
    suspend fun findCorrelations(since: Long, windowMs: Long): List<CrossModuleCorrelation> {
        val secEvents = securityEventRepository.getRecentEventsList(since)
        val telemetry = telemetryRepository.getRecentList(since)
        val faults = faultRepository.getRecentList(since)
        
        // For each security event, check if any PumpIQ event falls within ±windowMs
        // ...
    }
}
```

## Test Requirements
- Person at 22:14 + power outage telemetry at 22:16 → correlation found, type = PERSON_NEAR_POWER_FAILURE
- Tampering at 03:00 + dry-run fault at 03:02 → correlation found, severity = CRITICAL
- Person at 10:00, no PumpIQ events near → no correlation
- No security events → empty list
- No PumpIQ events → empty list
