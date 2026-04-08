# FIX-08: Add Surakṣā Context to SystemPromptBuilder

**Severity:** 🟡 IMPORTANT  
**Size:** M (2-4h)  
**Dependencies:** FIX-07

## Problem
`SystemPromptBuilder` only includes PumpIQ motor state and predictions. No security events, camera status, briefings, or cross-module correlations are injected into the 3B model's system prompt. Chat queries about security get no context.

## Acceptance Criteria
- [ ] AC1: System prompt includes "MODULE: Surakṣā" section with: active camera count, last 24h event summary (counts by threat level), latest event description
- [ ] AC2: If cross-module correlations exist, they appear in a "CROSS-MODULE CORRELATIONS" section
- [ ] AC3: Latest security briefing summary included if available
- [ ] AC4: RAG pipeline searches both PumpIQ and Surakṣā vector store namespaces
- [ ] AC5: If no Surakṣā data exists → section says "No security events recorded" (no crash)
- [ ] AC6: Prompt total length stays under 4096 tokens (truncate oldest events if needed)
- [ ] AC7: Unit tests verify prompt contains Surakṣā sections when data exists

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/prompt/SystemPromptBuilder.kt
app/src/test/java/com/ksetrasevakah/feature/pumpiq/chat/prompt/SystemPromptBuilderTest.kt
```

## Test Requirements
- With security events → prompt contains "[MODULE: Surakṣā]" section
- With correlations → prompt contains "CROSS-MODULE CORRELATIONS" section
- Empty Surakṣā data → section says "No security events"
- Both modules active → prompt has both sections
