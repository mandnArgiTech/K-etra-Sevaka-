# WIRE-03: Rewire Ingestion Pipeline — SMS → Parse → DB + Faults + Motor State + Worker Activity

**Severity:** 🔴 CRITICAL  
**Size:** L (4-8h)  
**Dependencies:** WIRE-01, WIRE-02

## Problem
`SmsTelemetryProcessor.process()` only inserts a `TelemetryEntity` row. It does NOT:
- Update `MotorStateEntity` when Motor ON/OFF SMS arrives
- Create `FaultEntity` when Dryrun/PhaseFailure/Overload/LowVoltage arrives
- Update `WorkerActivityEntity` for daily ON/OFF tracking
- Update `PowerStateEntity` for Power Failure/Resumed
- Generate a meaningful narrative for vector DB embedding

## Acceptance Criteria
- [ ] AC1: `SmsTelemetryProcessor` accepts `TaroPanelEvent` (from WIRE-01 parser)
- [ ] AC2: Every event → `PanelEventEntity` inserted (unified log)
- [ ] AC3: `MotorOn` event → `MotorStateEntity.state = "ON"`, `currentSessionStart = timestamp`
- [ ] AC4: `MotorOff` event → `MotorStateEntity.state = "OFF"`, `lastOffTime = timestamp`
- [ ] AC5: `MotorOn/Off` → `WorkerActivityEntity` upserted with onTime/offTime, source, cycle
- [ ] AC6: `Dryrun` → `FaultEntity` inserted with faultType="DRYRUN", current values in description
- [ ] AC7: `PhaseFailure` → `FaultEntity` with faultType="PHASE_FAILURE_Y" or "PHASE_FAILURE_R/B"
- [ ] AC8: `Overload` → `FaultEntity` with faultType="OVERLOAD"
- [ ] AC9: `LowVoltage` → `FaultEntity` with faultType="LOW_VOLTAGE", voltages in description
- [ ] AC10: `PowerFailure` → `PowerStateEntity.isOn = false`, increment failureCountToday
- [ ] AC11: `PowerResumed` / `DevicePoweredOn` → `PowerStateEntity.isOn = true`
- [ ] AC12: Narrative generated for every event (1-2 sentences, human-readable)
- [ ] AC13: Narrative embedded into vector DB with `module = "PumpIQ"` metadata
- [ ] AC14: `CommandNotMatched` / `Unknown` → logged but no state changes
- [ ] AC15: All operations are transactional — if DB write fails, no partial state
- [ ] AC16: Unit tests with all 11 event types verify correct DB side-effects

## Narrative Generation (No AI — template-based)

```kotlin
fun generateNarrative(event: TaroPanelEvent): String = when (event) {
    is TaroPanelEvent.MotorOn -> 
        "Motor turned ON via ${event.via.name} at ${formatTime(event.timestamp)}."
    is TaroPanelEvent.MotorOff -> 
        "Motor turned OFF via ${event.via.name} at ${formatTime(event.timestamp)}."
    is TaroPanelEvent.PowerFailure -> 
        "Power failure detected at ${formatTime(event.timestamp)}. Grid supply lost."
    is TaroPanelEvent.PowerResumed -> 
        "Power supply resumed at ${formatTime(event.timestamp)}."
    is TaroPanelEvent.DevicePoweredOn -> 
        "Panel powered on at ${formatTime(event.timestamp)}. Motor status: ${if (event.motorStatus) "ON" else "OFF"}, Mode: ${event.mode}."
    is TaroPanelEvent.LowVoltage -> 
        "Low voltage alert at ${formatTime(event.timestamp)}. Voltage R=${event.voltageR}V, Y=${event.voltageY}V, B=${event.voltageB}V."
    is TaroPanelEvent.Dryrun -> 
        "Dryrun fault at ${formatTime(event.timestamp)}. Current R=${event.currentR}A, Y=${event.currentY}A, B=${event.currentB}A."
    is TaroPanelEvent.PhaseFailure -> 
        "${event.failedPhases} phase failure at ${formatTime(event.timestamp)}. Voltage R=${event.voltageR}V, Y=${event.voltageY}V, B=${event.voltageB}V."
    is TaroPanelEvent.Overload -> 
        "Overload fault at ${formatTime(event.timestamp)}. Current R=${event.currentR}A, Y=${event.currentY}A, B=${event.currentB}A."
    is TaroPanelEvent.CommandNotMatched -> 
        "Command not matched by panel at ${formatTime(event.timestamp)}."
    is TaroPanelEvent.Unknown -> 
        "Unknown panel event at ${formatTime(event.timestamp)}: ${event.rawSms.take(100)}"
}
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/ai/SmsTelemetryProcessor.kt  ← full rewrite
app/src/main/java/com/ksetrasevakah/core/sms/MotorStateMachine.kt     ← simplify, driven by events
```

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/ai/NarrativeTemplateGenerator.kt
```

## Test Requirements
- MotorOn event → MotorStateEntity.state = "ON" (verify via DAO mock)
- MotorOff event → MotorStateEntity.state = "OFF"
- Dryrun event → FaultEntity inserted with type "DRYRUN"
- LowVoltage event → FaultEntity inserted with voltages in description
- PowerFailure → PowerStateEntity.isOn = false
- PowerResumed → PowerStateEntity.isOn = true
- MotorOn → WorkerActivityEntity created with onTime and source
- MotorOff → WorkerActivityEntity updated with offTime
- Narrative generated for every event type (non-null, non-empty)
- Vector DB ingest called with narrative text

## Definition of Done
- Every SMS type flows through to the correct DB tables
- Motor state updates reactively
- Faults are auto-extracted
- Worker activity tracks every ON/OFF cycle
- Power state tracked
