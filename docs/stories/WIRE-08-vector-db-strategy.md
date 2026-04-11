# WIRE-08: Vector DB Strategy — What to Embed, When, and How to Query

**Severity:** 🟡 IMPORTANT  
**Size:** M (2-4h)  
**Dependencies:** WIRE-03

## Problem
Current vector DB usage is wrong:
- Raw SMS text is embedded ("Taro Smart Panel (3L):\n\nAttention!...") — this is structured data, not semantic text. Vector similarity search on it is meaningless.
- No metadata attached to embeddings — can't filter by date, event type, or module.
- RAG queries return irrelevant results because the embeddings don't represent meaningful knowledge.

## Architecture Decision: What Goes Where

```
┌─────────────────────────────────────────────────────┐
│ STRUCTURED DATA → SQLite (Room)                     │
│                                                     │
│ - Timestamps, voltages, currents (exact values)     │
│ - Event types, motor state, fault types             │
│ - Worker ON/OFF times, power failure times           │
│ - Anything you query by exact value or range         │
│                                                     │
│ QUERIES: "voltage readings between 8am-10am"        │
│          "how many dryruns this week?"               │
│          "what time did worker start today?"         │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│ SEMANTIC KNOWLEDGE → Vector DB (FAISS)              │
│                                                     │
│ - Generated NARRATIVES (human-readable sentences)   │
│ - Cross-event correlations and patterns             │
│ - AI-generated briefings and summaries              │
│ - Anything you query by MEANING                     │
│                                                     │
│ QUERIES: "tell me about power problems this week"   │
│          "what happened around the dryrun?"          │
│          "any unusual activity yesterday?"           │
└─────────────────────────────────────────────────────┘
```

## Acceptance Criteria
- [ ] AC1: Embed NARRATIVE text (from WIRE-03 NarrativeTemplateGenerator), NOT raw SMS
- [ ] AC2: Each embedding has metadata: `{module: "PumpIQ", event_type: "DRYRUN", timestamp: 1234567, date: "2026-04-11"}`
- [ ] AC3: RAG query pre-filters by module namespace before similarity search
- [ ] AC4: RAG query returns top-5 most relevant narratives with timestamps
- [ ] AC5: Briefings and cross-module correlation descriptions also embedded
- [ ] AC6: Vector store persisted to disk — survives app restart
- [ ] AC7: Vector store rebuilt from PanelEvent narratives if index corrupted
- [ ] AC8: Embedding model uses the sentence-transformers MiniLM (already downloaded in setup)
- [ ] AC9: Embedding happens async after DB insert — never blocks SMS processing

## What Gets Embedded (ordered by priority)
```
1. Event narratives (every SMS → 1 narrative sentence)
   "Motor turned ON via Keypad at 08:43 on April 11."
   
2. Correlation narratives (from CrossModuleCorrelator)
   "Power failure at 10:44 on April 11 occurred while motor was running since 08:43."
   
3. Daily summaries (generated at end of day)
   "April 10: Motor ran 6h across 3 sessions. 4 power failures. 2 low voltage alerts."
   
4. Security briefings (from Suraksha)
   "Night watch: 3 person detections at Front Gate between 22:00-23:00."
```

## What DOES NOT Get Embedded
```
- Raw SMS text (structured, not semantic)
- Numeric values (use SQL queries instead)
- Duplicate narratives (deduplicate before embedding)
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/vectorstore/VectorStoreManager.kt   ← add metadata
app/src/main/java/com/ksetrasevakah/core/vectorstore/RagPipeline.kt          ← add namespace filter
app/src/main/java/com/ksetrasevakah/core/ai/SmsTelemetryProcessor.kt         ← embed narrative, not raw SMS
```

## Test Requirements
- Narrative embedded → similarity search for "power failure" returns power-related narratives
- Namespace filter → PumpIQ query doesn't return Suraksha narratives
- Metadata preserved → returned results include timestamp and event_type
- Raw SMS NOT embedded (verify ingest receives narrative, not rawSms)
- Corrupted index → rebuild from DB narratives succeeds
