# E08-S03: 3B Model Integration with Context & RAG

**Epic:** 08 — AI Chat  
**Size:** L (4-8h)  
**Dependencies:** E08-S02, E04-S01, E09-S01

## Description
Wire the 3B orchestrator model into the chat flow: build system prompt with current motor state + recent telemetry, load conversation context (last 10 messages), run RAG retrieval from vector store, and stream the response.

## Acceptance Criteria
- [ ] AC1: `ChatOrchestrator` class builds complete prompt for 3B model
- [ ] AC2: System prompt includes: current motor state, phase currents, predictions summary, today's worker activity
- [ ] AC3: Last 10 messages from thread loaded as conversation history
- [ ] AC4: User query embedded (via 0.5B) → FAISS top-5 retrieval → context chunks injected
- [ ] AC5: 3B model generates response as streaming `Flow<String>`
- [ ] AC6: Streamed tokens displayed incrementally in chat bubble (typewriter effect)
- [ ] AC7: Complete response saved to `ChatMessageEntity` when generation finishes
- [ ] AC8: 3B model lazy-loaded on first query, unloaded after 5 min idle
- [ ] AC9: Generation error → user-friendly error message in chat
- [ ] AC10: Model loading state shown as "Loading AI model..." indicator

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/
├── ChatOrchestrator.kt
├── prompt/
│   ├── SystemPromptBuilder.kt
│   └── ContextAssembler.kt

app/src/test/java/com/ksetrasevakah/feature/pumpiq/chat/ChatOrchestratorTest.kt
app/src/test/java/com/ksetrasevakah/feature/pumpiq/chat/prompt/SystemPromptBuilderTest.kt
```

## Implementation Details
### System Prompt Template
```
You are PumpIQ AI Analyst, an intelligent assistant for agricultural motor management.

CURRENT STATE:
- Motor: {ON/OFF} since {time}
- Phase Currents: R={r}A, Y={y}A, B={b}A
- Voltage: {v}V
- Today's Worker: ON at {time}, expected OFF at {time}

PREDICTIONS:
- Power failure risk: {time} ({confidence}%)
- Next fault risk: {type} in {hours}h ({risk}%)
- Tomorrow worker ON: {time}
- Forgot-OFF risk: {percent}%

RELEVANT CONTEXT FROM HISTORY:
{rag_chunks}

Answer the user's question using the above data. Be specific with numbers and times. 
If recommending actions, be clear and actionable for a farm worker.
```

## Test Requirements
### SystemPromptBuilderTest
- Motor ON → prompt contains "Motor: ON since..."
- Motor OFF → prompt contains "Motor: OFF"
- Predictions included with correct values
- RAG chunks injected between markers

### ChatOrchestratorTest
- Query with context → 3B model receives full prompt
- Empty RAG results → prompt works without context section
- Model not loaded → triggers load, then generates
- Generation error → returns error message
