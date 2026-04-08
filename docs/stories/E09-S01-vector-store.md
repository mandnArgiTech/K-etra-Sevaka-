# E09-S01: FAISS Vector Store Setup & Embedding Pipeline

**Epic:** 09 — Vector Store & RAG  
**Size:** L (4-8h)  
**Dependencies:** E04-S01

## Description
Set up local FAISS vector store for storing narrative text embeddings. The 0.5B model generates embeddings; FAISS indexes them for similarity search.

## Acceptance Criteria
- [ ] AC1: `VectorStoreManager` initializes FAISS index on app start
- [ ] AC2: `addDocument(text: String, metadata: Map)` embeds text via 0.5B and adds to index
- [ ] AC3: `search(query: String, topK: Int): List<VectorSearchResult>` returns similar docs
- [ ] AC4: Index persisted to disk at `app_data/vector_store/`
- [ ] AC5: Index rebuilt from Room narratives if file missing/corrupted
- [ ] AC6: Metadata stored alongside (timestamp, telemetry_id, source)
- [ ] AC7: Embedding dimension matches 0.5B model output (384 or model-specific)
- [ ] AC8: Thread-safe — concurrent add/search don't corrupt index
- [ ] AC9: Unit tests with mock embeddings pass

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/vectorstore/
├── VectorStoreManager.kt
├── EmbeddingGenerator.kt            # Uses 0.5B model for embeddings
├── model/VectorSearchResult.kt
└── di/VectorStoreModule.kt

app/src/test/java/com/ksetrasevakah/core/vectorstore/VectorStoreManagerTest.kt
app/src/test/java/com/ksetrasevakah/core/vectorstore/EmbeddingGeneratorTest.kt
```

## Test Requirements
- Add 5 documents → search returns relevant results
- Search with no matches → empty list
- Persist + reload index → search still works
- Concurrent add operations → no corruption
