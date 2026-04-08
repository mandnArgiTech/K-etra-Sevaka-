# E13-S02: 0.5B Threat Router — Classification, Routing & Notification Dismissal

**Epic:** 13 — Surakṣā Notification Engine  
**Size:** L (4-8h)  
**Dependencies:** E13-S01, E04-S01 (MLC-LLM engine)

## Description
The 0.5B model classifies each Tapo event into threat levels (LOW/MEDIUM/HIGH/CRITICAL), generates a narrative, and routes the event: silent dismiss, standard notify, or critical alarm. Includes activity spike detection for coordinated intrusion escalation.

## Acceptance Criteria
- [ ] AC1: `ThreatRouter` class receives `TapoEvent`, queries recent event context, calls 0.5B model
- [ ] AC2: 0.5B prompt includes: cameraName, eventType, hourOfDay, recentEventsCount (same cam 5min), recentEventsAllCams (2min)
- [ ] AC3: Classification matrix: PERSON+day=LOW, PERSON+evening=MEDIUM, PERSON+night=HIGH, TAMPERING=CRITICAL
- [ ] AC4: Activity spike: ≥3 events same camera in 5 min → elevate threat by 1 level
- [ ] AC5: Coordinated intrusion: ≥2 different cameras triggered within 2 min at night → CRITICAL
- [ ] AC6: LOW/MEDIUM → `SilentIngest`: save to DB + vector store, cancel notification via `cancelNotification(sbnKey)`
- [ ] AC7: HIGH → `StandardNotify`: save to DB + vector store, notification stays
- [ ] AC8: CRITICAL → `CriticalAlarm`: save to DB + vector store, trigger alarm + overlay
- [ ] AC9: Camera mode SILENT → always SilentIngest regardless of threat level (override)
- [ ] AC10: 0.5B generates 1-sentence narrative saved to `SecurityEventEntity.narrative`
- [ ] AC11: Narrative also embedded into FAISS vector store for RAG
- [ ] AC12: Handles 0.5B model failure gracefully — falls back to rule-based classification
- [ ] AC13: All classification logic has unit tests with deterministic inputs

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/suraksha/prediction/
├── ThreatRouter.kt
├── ThreatClassifier.kt                  # Rule-based + AI hybrid
├── ActivitySpikeDetector.kt
└── model/ThreatAction.kt                # sealed: SilentIngest, StandardNotify, CriticalAlarm

app/src/main/java/com/ksetrasevakah/core/ai/prompt/
└── ThreatRouterPrompt.kt

app/src/test/java/com/ksetrasevakah/feature/suraksha/prediction/
├── ThreatRouterTest.kt
├── ThreatClassifierTest.kt
└── ActivitySpikeDetectorTest.kt
```

## Implementation Details

### ThreatClassifier — Hybrid Classification
```kotlin
class ThreatClassifier @Inject constructor(
    private val mlcEngine: MlcLlmEngine,
    private val securityEventDao: SecurityEventDao,
) {
    /**
     * RULE-BASED FAST PATH (used when 0.5B is busy or fails):
     */
    fun classifyByRules(event: TapoEvent, context: EventContext): ThreatLevel {
        // Base classification
        val base = when {
            event.eventType == EventType.TAMPERING -> ThreatLevel.CRITICAL
            event.eventType == EventType.PERSON -> when (event.hourOfDay) {
                in 6..17 -> ThreatLevel.LOW
                in 18..21 -> ThreatLevel.MEDIUM
                else -> ThreatLevel.HIGH // 22:00-05:59
            }
            else -> ThreatLevel.MEDIUM
        }
        
        // Elevation factors
        var level = base
        if (context.sameCamera5MinCount >= 3) level = level.elevate()
        if (context.allCameras2MinCount >= 2 && event.isNighttime) level = ThreatLevel.CRITICAL
        if (context.cameraPreviouslyTampered) level = maxOf(level, ThreatLevel.HIGH)
        
        return level
    }
    
    /**
     * AI-ENHANCED PATH: 0.5B refines classification + generates narrative
     */
    suspend fun classifyWithAi(event: TapoEvent, context: EventContext): ClassificationResult {
        val ruleResult = classifyByRules(event, context)
        return try {
            val aiResult = mlcEngine.generate(
                ThreatRouterPrompt.build(event, context), 
                Constants.INGESTION_MODEL_ID
            )
            parseAiResponse(aiResult, ruleResult) // AI can elevate but never lower below rules
        } catch (e: Exception) {
            ClassificationResult(ruleResult, "Classification by rules (AI unavailable)", false)
        }
    }
}
```

### EventContext — Recent Activity Lookup
```kotlin
data class EventContext(
    val sameCamera5MinCount: Int,
    val allCameras2MinCount: Int,
    val cameraPreviouslyTampered: Boolean,
    val hourOfDay: Int,
    val isNighttime: Boolean, // 22:00-05:59
)
```

### Notification Dismissal (Anti-Fatigue)
```kotlin
// In ThreatRouter, after classification:
when {
    cameraMode == CameraMode.SILENT -> {
        // ALWAYS dismiss for SILENT cameras
        notificationListener.cancelNotification(event.sbnKey)
    }
    threatLevel <= ThreatLevel.MEDIUM -> {
        // LOW/MEDIUM: dismiss to prevent farmer fatigue
        notificationListener.cancelNotification(event.sbnKey)
    }
    // HIGH/CRITICAL: notification STAYS visible
}
```

## Test Requirements

### ThreatClassifierTest (Unit — deterministic, no AI)
- PERSON at 10:00 (day) → LOW
- PERSON at 20:00 (evening) → MEDIUM
- PERSON at 02:00 (night) → HIGH
- TAMPERING at any time → CRITICAL
- PERSON at day + 3 spikes same camera → MEDIUM (elevated from LOW)
- PERSON at night + 2 different cameras in 2min → CRITICAL (coordinated)
- Camera previously tampered + new PERSON → at least HIGH
- UNKNOWN event type → MEDIUM default

### ActivitySpikeDetectorTest
- 2 events in 5 min → isSpike = false
- 3 events in 5 min same camera → isSpike = true
- 3 events in 6 min → isSpike = false (outside window)
- 2 different cameras in 2 min at night → isCoordinated = true
- 2 different cameras in 3 min → isCoordinated = false

### ThreatRouterTest (Integration, mocked AI + DAOs)
- LOW event, ACTIVE mode → SilentIngest + cancelNotification called
- HIGH event, ACTIVE mode → StandardNotify + cancelNotification NOT called
- CRITICAL event → CriticalAlarm action
- Any event, SILENT mode → SilentIngest regardless of threat level
- Event saved to SecurityEventDao with correct fields
- Narrative generated and stored
- Vector store addDocument called with narrative
- 0.5B failure → rule-based fallback used, no crash

## Definition of Done
- Threat classification is deterministic for rule-based path
- AI can only elevate, never lower below rules
- Notification dismissal works for LOW/MEDIUM/SILENT
- All 15+ tests pass
