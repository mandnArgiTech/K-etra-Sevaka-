# E16-S01: Cross-Module AI Chat — PumpIQ + Surakṣā Forensic Analysis

**Epic:** 16 — Cross-Module Intelligence  
**Size:** L (4-8h)  
**Dependencies:** E08-S03, E12-S01, E13-S02

## Description
Extend the existing AI Analyst chat to support cross-module queries. The 3B model can now correlate Surakṣā security events with PumpIQ telemetry data. Chat UI visually distinguishes cross-module data sources with module badges.

## Acceptance Criteria
- [ ] AC1: `SystemPromptBuilder` extended to inject Surakṣā context alongside PumpIQ context
- [ ] AC2: System prompt includes: recent security events (last 24h summary), active camera count, latest briefing, current threat status
- [ ] AC3: `CrossModuleCorrelator` finds events within ±5 min window: security event ↔ power failure/motor change
- [ ] AC4: Correlations injected into 3B system prompt for forensic reasoning
- [ ] AC5: New quick query chips added: "🛡️ Security Briefing", "🔍 Cross-Module Analysis", "📹 Camera Status", "⚠️ Last CRITICAL Event"
- [ ] AC6: Quick chips are module-aware: PumpIQ chips (green), Surakṣā chips (blue), cross-module chips (purple)
- [ ] AC7: AI responses that reference both modules show inline module badges: [PumpIQ] and [Surakṣā]
- [ ] AC8: Chat message bubbles from Surakṣā queries use blue "SURAKṢĀ AI" label instead of green "PUMPIQ AI"
- [ ] AC9: Cross-module responses use purple "FORENSIC AI" label
- [ ] AC10: RAG pipeline searches both PumpIQ and Surakṣā vector store segments
- [ ] AC11: Tapping Surakṣā dashboard ledger item opens chat with pre-filled forensic query

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/
├── prompt/SystemPromptBuilder.kt          ← MODIFY (add Surakṣā context)
├── ChatOrchestrator.kt                    ← MODIFY (cross-module RAG)
├── component/QuickQueryChips.kt           ← MODIFY (add Surakṣā chips)
└── component/MessageBubble.kt             ← MODIFY (module-aware label/color)
```

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/suraksha/prediction/
├── CrossModuleCorrelator.kt
└── SecurityBriefingGenerator.kt

app/src/main/java/com/ksetrasevakah/core/ai/prompt/
└── SecurityBriefingPrompt.kt

app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/model/
└── MessageSource.kt                       # enum: PUMPIQ, SURAKSHA, CROSS_MODULE

app/src/test/java/com/ksetrasevakah/feature/suraksha/prediction/
├── CrossModuleCorrelatorTest.kt
└── SecurityBriefingGeneratorTest.kt
```

## Implementation Details

### Extended System Prompt
```
You are the Kṣetra Sevakaḥ AI Analyst with access to two modules:

[MODULE: PumpIQ] Motor and power management:
- Motor: {state} since {time}
- Phase Currents: R={r}A, Y={y}A, B={b}A
- Today's Worker: ON at {time}
{pumpiq_predictions}

[MODULE: Surakṣā] Security camera intelligence:
- Active cameras: {count}/{total} ({active_names})
- Last 24h: {event_count} events ({critical}C, {high}H, {medium}M, {low}L)
- Latest event: {event_description} at {time}
- Active threats: {active_threats_or_none}
{security_briefing_summary}

[CROSS-MODULE CORRELATIONS]:
{correlations_or_none}

RELEVANT HISTORY (RAG):
{rag_chunks_from_both_modules}

Answer the user's question. When referencing data, prefix with [PumpIQ] or [Surakṣā].
For questions spanning both modules, analyze the correlation and prefix with [Forensic].
```

### CrossModuleCorrelator
```kotlin
/**
 * Finds temporal correlations:
 * - Security event within ±5 min of power outage → possible sabotage
 * - Security event within ±5 min of motor start/stop at odd hour → unauthorized
 * - Camera tampering + any PumpIQ anomaly → coordinated attack
 */
suspend fun findCorrelations(since: Long, windowMs: Long = 300_000): List<Correlation>
```

### Chat Quick Chips — Module Colors
```
PumpIQ (green accent):     "⚡ Power Failure?", "🔧 Next Fault?", "📊 24h Summary"
Surakṣā (blue accent):    "🛡️ Security Briefing", "📹 Camera Status", "⚠️ Last CRITICAL"
Cross-Module (purple):     "🔍 Cross-Module Analysis", "🕵️ Forensic Timeline"
```

### MessageBubble Module Badge
```kotlin
// Inside AI message bubble, before the response text:
when (message.source) {
    PUMPIQ -> Badge("PUMPIQ AI", color = KsetraAccentGreen, icon = "zap")
    SURAKSHA -> Badge("SURAKṢĀ AI", color = KsetraShieldBlue, icon = "shield")
    CROSS_MODULE -> Badge("FORENSIC AI", color = KsetraPurple, icon = "brain")
}
```

## Test Requirements

### CrossModuleCorrelatorTest
- Person at 22:14 + power failure at 22:16 → correlation found, severity HIGH
- Person at 10:00 + motor start at 10:01 → correlation found, severity LOW (daytime)
- Person at 22:00, no PumpIQ event within 5 min → no correlation
- Tampering + any PumpIQ anomaly → severity CRITICAL
- Empty security events → empty correlations

### SecurityBriefingGeneratorTest
- 10 events, 2 critical → briefing mentions critical events with timestamps
- Cross-module correlations found → briefing includes "Cross-Module" section
- No events → briefing says "Quiet night, no events"
- Briefing saved to SecurityBriefingEntity

### SystemPromptBuilderTest (Extended)
- Surakṣā context included when events exist
- Correlations section populated when correlations found
- Empty Surakṣā → section says "No security events"
- Both modules active → full prompt with all sections

## Definition of Done
- Chat seamlessly handles PumpIQ, Surakṣā, and cross-module queries
- Module badges visually distinguish response sources
- Cross-module correlations surfaced proactively
- RAG searches both module vector stores
- All tests pass
