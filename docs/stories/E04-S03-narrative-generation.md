# E04-S03: Narrative Text Generation & Vector Store Ingestion

**Epic:** 04 — AI Ingestion Layer  
**Size:** M (2-4h)  
**Dependencies:** E04-S02

## Description
After extracting structured data, the 0.5B model generates a human-readable narrative from each telemetry record. This narrative is embedded and stored in the FAISS vector store for RAG retrieval by the 3B orchestrator.

## Acceptance Criteria
- [ ] AC1: `NarrativeGenerator` uses 0.5B model to create narrative from TelemetryEntity
- [ ] AC2: Generated narrative is stored in `TelemetryEntity.narrative` field
- [ ] AC3: Narrative is passed to `VectorStoreRepository.addDocument()` for embedding
- [ ] AC4: Narratives include temporal context ("At 14:32 on April 8th, a dry run was detected...")
- [ ] AC5: Generation prompt produces consistent, concise narratives (1-3 sentences)
- [ ] AC6: Handles generation failure gracefully — stores telemetry even if narrative fails
- [ ] AC7: Unit tests pass for narrative prompt formatting and error handling

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/ai/
├── NarrativeGenerator.kt
└── prompt/NarrativePrompt.kt

app/src/test/java/com/ksetrasevakah/core/ai/NarrativeGeneratorTest.kt
```

## Test Requirements
- Valid telemetry → narrative generated with timestamp context
- Fault telemetry → narrative mentions fault type and severity
- 0.5B model failure → telemetry saved without narrative (no crash)
- Narrative length < 500 chars enforced
