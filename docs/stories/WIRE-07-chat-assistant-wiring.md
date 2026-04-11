# WIRE-07: Wire Chat Assistant — System Prompt with Real Data, Response Generation

**Severity:** 🔴 CRITICAL  
**Size:** L (4-8h)  
**Dependencies:** WIRE-02, WIRE-03

## Problem
Chat assistant doesn't work because:
1. `SystemPromptBuilder` queries old repositories that return empty data
2. The LLM model may not be downloaded yet — no graceful fallback
3. Quick query chips fire but no meaningful response comes back
4. RAG pipeline has no data to retrieve (vector store empty)
5. No conversation context — model doesn't know what the farm panel data shows

## Acceptance Criteria
- [ ] AC1: SystemPromptBuilder queries `PanelEventDao` for last 24h events → builds context
- [ ] AC2: System prompt includes: current motor state, today's event count by type, latest fault, power status, last worker ON/OFF times
- [ ] AC3: Quick query "Summarize Last 24h" → generates summary from PanelEventDao data
- [ ] AC4: Quick query "Predict Power Failure" → uses PredictionEngine result in response
- [ ] AC5: Quick query "Worker ON Tomorrow?" → uses WorkerOnTimePredictor in response
- [ ] AC6: Quick query "Motor Health Score" → computed from fault frequency and voltage stability
- [ ] AC7: If LLM model not loaded → show "AI model loading..." then auto-retry
- [ ] AC8: If LLM model not downloaded → show "Download model in Settings" message
- [ ] AC9: If LLM fails → fallback to template-based response using DB data directly (no AI needed)
- [ ] AC10: RAG pipeline queries vector store for relevant historical narratives
- [ ] AC11: Free-text queries about specific events work: "What happened yesterday at 2pm?"
- [ ] AC12: Chat messages saved to ChatMessageEntity for thread history

## Template-Based Fallback Responses (when LLM unavailable)
```kotlin
// For "Summarize Last 24h" — no AI needed, pure DB query:
fun buildSummaryFromDb(): String {
    val motorOns = panelEventDao.getEventsByType("MOTOR_ON", since24h)
    val motorOffs = panelEventDao.getEventsByType("MOTOR_OFF", since24h)
    val faults = panelEventDao.getFaultEvents(since24h)
    val powerFailures = panelEventDao.getEventsByType("POWER_FAILURE", since24h)
    
    return buildString {
        appendLine("📊 Last 24 Hours Summary")
        appendLine("Motor: ${motorOns.size} starts, ${motorOffs.size} stops")
        appendLine("Power: ${powerFailures.size} failures")
        appendLine("Faults: ${faults.size} (${faults.groupBy{it.eventType}.map{"${it.key}:${it.value.size}"}.joinToString()})")
        // ... more details
    }
}
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/prompt/SystemPromptBuilder.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/ChatOrchestrator.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/ChatViewModel.kt
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/component/QuickQueryChips.kt
```

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/TemplateFallbackResponder.kt
```

## Test Requirements
- SystemPromptBuilder with real events → prompt contains motor state and event counts
- Quick query "Summarize 24h" with DB data → response includes event summary
- LLM unavailable → template fallback response returned (not empty, not error)
- Empty DB → response says "No data available yet"
- Chat message saved to DB after response generated
