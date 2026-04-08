# FIX-04: Add Time-of-Day Rule Classification for PERSON Events

**Severity:** 🔴 CRITICAL  
**Size:** M (2-4h)  
**Dependencies:** None

## Problem
`ThreatClassifier.applyRules()` only handles TAMPERING → CRITICAL. For PERSON events it returns `null`, falling through to the 3B AI model (`classifyWithAi`). This means every single PERSON detection across 30 cameras triggers a full LLM inference — an expensive, slow operation that will queue hundreds of AI calls during busy daytime hours.

The architecture spec defines clear rules:
- PERSON + 06:00–17:59 → LOW (daytime, expected)
- PERSON + 18:00–21:59 → MEDIUM (evening, caution)
- PERSON + 22:00–05:59 → HIGH (nighttime, alert)

These should be instant rule-based decisions (microseconds) — not 3B model calls (seconds each).

## Current Code (Broken)
```kotlin
// ThreatClassifier.kt
internal fun applyRules(event: TapoEvent): ThreatAction? {
    val eventType = event.eventType.uppercase()
    if (eventType == "TAMPERING") {
        return ThreatAction.CriticalAlarm(...)
    }
    return null  // ← PERSON falls through to 3B AI every time!
}
```

## Acceptance Criteria
- [ ] AC1: PERSON + hour 6-17 → `ThreatAction.LogOnly` with `ThreatLevel.LOW`, no AI call
- [ ] AC2: PERSON + hour 18-21 → `ThreatAction.LogOnly` with `ThreatLevel.MEDIUM`, no AI call
- [ ] AC3: PERSON + hour 22-23 or 0-5 → `ThreatAction.Notify` with `ThreatLevel.HIGH`, no AI call
- [ ] AC4: TAMPERING + any hour → `ThreatAction.CriticalAlarm` with `ThreatLevel.CRITICAL` (unchanged)
- [ ] AC5: UNKNOWN event type → falls through to AI classification (existing behavior, preserved)
- [ ] AC6: Activity spike (≥3 events same camera in 5 min) elevates threat by 1 level
- [ ] AC7: Coordinated intrusion (≥2 cameras within 2 min at night) → CRITICAL override
- [ ] AC8: AI model is ONLY called for UNKNOWN event types or when spike/coordination needs confirmation
- [ ] AC9: All classification logic has unit tests with deterministic hour inputs
- [ ] AC10: Performance: PERSON classification completes in < 1ms (no AI call)

## Fix — Updated applyRules()

```kotlin
internal suspend fun applyRules(event: TapoEvent): ThreatAction? {
    val eventType = event.eventType.uppercase()
    val hour = Calendar.getInstance().apply { timeInMillis = event.timestamp }
        .get(Calendar.HOUR_OF_DAY)

    // TAMPERING: always CRITICAL
    if (eventType == "TAMPERING") {
        return ThreatAction.CriticalAlarm(
            threatLevel = ThreatLevel.CRITICAL,
            confidence = 0.95f,
            summary = "Camera tampering detected on ${event.cameraName}",
            title = "CRITICAL: Camera Tampering",
            cameraName = event.cameraName
        )
    }

    // PERSON: classify by time-of-day
    if (eventType == "PERSON") {
        var level = when (hour) {
            in 6..17 -> ThreatLevel.LOW      // Daytime: expected
            in 18..21 -> ThreatLevel.MEDIUM   // Evening: caution
            else -> ThreatLevel.HIGH          // Night (22-5): alert
        }

        // Elevation: activity spike
        val isSpike = spikeDetector.isActivitySpike(event.cameraName, event.timestamp)
        if (isSpike) {
            level = level.elevate()  // LOW→MEDIUM, MEDIUM→HIGH, HIGH→CRITICAL
        }

        // Elevation: coordinated intrusion at night
        if (hour !in 6..21) {
            val isCoordinated = spikeDetector.isCoordinatedActivity(event.timestamp)
            if (isCoordinated) level = ThreatLevel.CRITICAL
        }

        val summary = buildPersonSummary(event.cameraName, hour, level, isSpike)
        
        return when {
            level.isCritical -> ThreatAction.CriticalAlarm(
                threatLevel = level, confidence = 0.9f,
                summary = summary,
                title = "CRITICAL: Coordinated Intrusion",
                cameraName = event.cameraName
            )
            level.isAlertable -> ThreatAction.Notify(
                threatLevel = level, confidence = 0.85f,
                summary = summary,
                title = "HIGH: Person at ${event.cameraName}"
            )
            else -> ThreatAction.LogOnly(
                threatLevel = level, confidence = 0.9f,
                summary = summary
            )
        }
    }

    // UNKNOWN: fall through to AI
    return null
}
```

### Add ThreatLevel.elevate() extension
```kotlin
// In ThreatLevel.kt
fun elevate(): ThreatLevel = when (this) {
    NONE -> LOW
    LOW -> MEDIUM
    MEDIUM -> HIGH
    HIGH -> CRITICAL
    CRITICAL -> CRITICAL
}
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/feature/suraksha/prediction/ThreatClassifier.kt
app/src/main/java/com/ksetrasevakah/feature/suraksha/domain/model/ThreatLevel.kt
```

## Test Requirements

### ThreatClassifierTest (update + add)
- PERSON at hour 10 → LogOnly, LOW, no AI call
- PERSON at hour 14 → LogOnly, LOW
- PERSON at hour 19 → LogOnly, MEDIUM
- PERSON at hour 23 → Notify, HIGH
- PERSON at hour 2 → Notify, HIGH
- PERSON at hour 10 + spike → LogOnly, MEDIUM (elevated)
- PERSON at hour 23 + spike → CriticalAlarm, CRITICAL (elevated from HIGH)
- PERSON at hour 1 + coordinated → CriticalAlarm, CRITICAL
- PERSON at hour 15 + coordinated → no elevation (daytime, not night)
- TAMPERING at hour 12 → CriticalAlarm, CRITICAL
- UNKNOWN at any hour → returns null (falls to AI)
- Verify `engine.generate()` is NEVER called for PERSON events

### ThreatLevelTest (add)
- LOW.elevate() == MEDIUM
- HIGH.elevate() == CRITICAL
- CRITICAL.elevate() == CRITICAL

## Definition of Done
- PERSON events classified in < 1ms without AI
- AI model only invoked for UNKNOWN events
- All 12+ tests pass
