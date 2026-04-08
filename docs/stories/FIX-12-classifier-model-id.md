# FIX-12: Use 0.5B Model for Threat Classification, Not 3B

**Severity:** 🟢 MINOR  
**Size:** S (< 15 min)  
**Dependencies:** None

## Problem
`ThreatClassifier.classifyWithAi()` uses `Constants.ORCHESTRATOR_MODEL_ID` (3B model). Per the architecture spec, the 0.5B Ingestion model should handle background classification. The 3B Orchestrator is reserved for frontend chat interactions.

Using the 3B model for every notification means:
- ~1.5GB GPU memory always occupied
- 5-second latency per classification instead of <1 second
- Blocks chat queries while classifying

## Acceptance Criteria
- [ ] AC1: `classifyWithAi()` uses `Constants.INGESTION_MODEL_ID` (0.5B)
- [ ] AC2: `ChatOrchestrator` continues to use `Constants.ORCHESTRATOR_MODEL_ID` (3B) — unchanged
- [ ] AC3: No other references to ORCHESTRATOR_MODEL_ID in the suraksha prediction package

## Fix — One Line Change
```kotlin
// ThreatClassifier.kt — change:
val response = engine.generate(prompt, Constants.ORCHESTRATOR_MODEL_ID)
// To:
val response = engine.generate(prompt, Constants.INGESTION_MODEL_ID)
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/feature/suraksha/prediction/ThreatClassifier.kt
```

## Test Requirements
- Verify `engine.generate()` is called with INGESTION_MODEL_ID in ThreatClassifierTest
