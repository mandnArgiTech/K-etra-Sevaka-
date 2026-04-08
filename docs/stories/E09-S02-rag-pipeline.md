# E09-S02: RAG Retrieval Pipeline for Chat

**Epic:** 09 — Vector Store & RAG  
**Size:** M (2-4h)  
**Dependencies:** E09-S01, E08-S03

## Description
Complete RAG pipeline: user query → embed → FAISS search → retrieve top-5 narrative chunks → inject into 3B model prompt.

## Acceptance Criteria
- [ ] AC1: `RagPipeline` class orchestrates embed → search → format flow
- [ ] AC2: User query embedded using same 0.5B model as document ingestion
- [ ] AC3: Top-5 results retrieved from FAISS with similarity scores
- [ ] AC4: Results filtered by minimum similarity threshold (> 0.3)
- [ ] AC5: Retrieved chunks formatted as context block for system prompt
- [ ] AC6: Each chunk includes timestamp and source info
- [ ] AC7: Empty retrieval → prompt works without RAG context (graceful fallback)
- [ ] AC8: Pipeline completes in < 2 seconds for typical queries

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/vectorstore/
└── RagPipeline.kt

app/src/test/java/com/ksetrasevakah/core/vectorstore/RagPipelineTest.kt
```

## Test Requirements
- Query with matching docs → top-5 returned with scores
- Query with no matches → empty context, no error
- Similarity threshold filters low-quality matches
- Formatted output includes timestamps
