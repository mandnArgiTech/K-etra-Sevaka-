# E14-S01: Surakṣā Dashboard Screen — Threat Ledger & Briefing

**Epic:** 14 — Surakṣā UI  
**Size:** L (4-8h)  
**Dependencies:** E12-S01, E13-S02, E05-S01

## Description
Build the Surakṣā Dashboard: a scrollable screen with threat summary cards (counts by level), the color-coded Threat Ledger (timeline of security events), an AI Security Briefing card, and an hourly activity heatmap. The dashboard observes real-time security events via Flow.

## Acceptance Criteria
- [ ] AC1: `SurakshaDashboardViewModel` collects from SecurityEventRepository, CameraConfigRepository, BriefingRepository
- [ ] AC2: `SurakshaDashboardUiState` contains: threatCounts, ledgerEvents, briefing, heatmapData, unacknowledgedCount, criticalOverlay
- [ ] AC3: Header shows "Surakṣā" title with shield-blue accent, back arrow → Hub, camera-matrix icon → Camera Matrix screen
- [ ] AC4: **Threat Summary Row:** 4 mini cards showing today's counts — LOW (green), MEDIUM (amber), HIGH (red), CRITICAL (dark red) — each with count number
- [ ] AC5: **Threat Ledger** (main section): Scrollable vertical timeline of events, newest first
- [ ] AC6: Each ledger item shows: colored threat dot, camera name, event type icon (person/tamper), origin timestamp (HH:MM:SS format — the NVR-scrubbable time), threat level badge, and 0.5B narrative snippet
- [ ] AC7: Ledger items are color-coded by threat: green left-border for LOW, amber for MEDIUM, red for HIGH, pulsing red for CRITICAL
- [ ] AC8: **AI Security Briefing Card:** Shows latest nightly briefing summary, with "Generate Briefing" button for on-demand
- [ ] AC9: Briefing card shows cross-module correlations if found (e.g., "Power failure + person detected")
- [ ] AC10: **Hourly Heatmap:** 24-column bar chart (0-23h), bar height = event count, color intensity by threat mix
- [ ] AC11: Tapping a ledger item → navigates to AI chat with query "Tell me about the {event_type} at {camera_name} at {time}"
- [ ] AC12: Badge on Hub's Surakṣā module card shows unacknowledged HIGH+CRITICAL count
- [ ] AC13: Screen auto-updates when new events arrive (Flow collection)
- [ ] AC14: Empty state: "No security events yet. Surakṣā is monitoring your cameras."

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/suraksha/dashboard/
├── SurakshaDashboardScreen.kt
├── SurakshaDashboardViewModel.kt
├── model/
│   ├── SurakshaDashboardUiState.kt
│   └── SurakshaDashboardUiEvent.kt
└── component/
    ├── ThreatSummaryRow.kt              # 4 mini count cards
    ├── ThreatLedger.kt                  # Scrollable event timeline
    ├── ThreatLedgerItem.kt              # Single event row
    ├── SecurityBriefingCard.kt          # AI briefing card
    └── ThreatHeatmap.kt                 # 24-hour bar chart

app/src/test/java/com/ksetrasevakah/feature/suraksha/dashboard/
└── SurakshaDashboardViewModelTest.kt

app/src/androidTest/java/com/ksetrasevakah/feature/suraksha/dashboard/
├── SurakshaDashboardScreenTest.kt
└── ThreatLedgerItemTest.kt
```

## Implementation Details

### ThreatLedgerItem Visual Spec
```
┌─ threat color bar (4px wide, full height) ────────────────────────┐
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │ [●] Front Gate                              06:14:32 AM     │ │
│  │  🚶 Person Detected                        ┌────────┐      │ │
│  │  "Worker approached the main entrance..."   │ MEDIUM │      │ │
│  │                                             └────────┘      │ │
│  └──────────────────────────────────────────────────────────────┘ │
└───────────────────────────────────────────────────────────────────┘

- Threat color bar: GREEN (LOW), AMBER (MEDIUM), RED (HIGH), PULSING RED (CRITICAL)
- Timestamp: origin_timestamp formatted as HH:MM:SS — exact NVR scrub time
- Event icon: 🚶 for PERSON, ⚠ for TAMPERING
- Narrative: truncated to 1 line with "..."
- Risk badge: colored per threat level
```

### ThreatHeatmap — 24-Hour Activity Chart
```
Each bar = events in that hour
Color = dominant threat level in that hour
Height = event count (scaled to tallest)
Hours 22-05 highlighted with subtle purple background (night zone)
```

## Test Requirements

### SurakshaDashboardViewModelTest
- Initial state = Loading
- Events flow emission → UiState.ledgerEvents updated
- Threat distribution flow → summaryCards updated
- Empty events → empty state message set
- New event arrives → ledger auto-prepends
- Unacknowledged count updates badge

### SurakshaDashboardScreenTest (Compose)
- Threat summary row shows 4 cards with counts
- Ledger renders event items with camera names
- Briefing card visible with summary text
- Heatmap renders 24 bars
- Empty state message when no events
- Tap ledger item → navigation event dispatched

### ThreatLedgerItemTest
- HIGH threat → red left border
- CRITICAL threat → pulsing animation
- Origin timestamp formatted as HH:MM:SS
- Narrative text truncated to single line

## Definition of Done
- Dashboard scrolls smoothly with 100+ ledger items
- Real-time updates via Flow
- All visual specs match design tokens
- All tests pass
