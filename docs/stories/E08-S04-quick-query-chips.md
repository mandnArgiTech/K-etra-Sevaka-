# E08-S04: One-Click Prediction Query Chips

**Epic:** 08 — AI Chat  
**Size:** M (2-4h)  
**Dependencies:** E08-S02

## Description
Horizontally scrollable quick action chips at top of chat screen. Prediction chips (purple) and data chips (green). Tapping a chip auto-sends that query to the AI.

## Acceptance Criteria
- [ ] AC1: Chip bar below header, horizontally scrollable (wrapping to 2 rows max)
- [ ] AC2: 10 chips total: 5 prediction (purple) + 5 data (green)
- [ ] AC3: Prediction chips: "⚡ Power Failure?", "🔧 Next Fault?", "👤 Worker ON Tomorrow?", "⚠️ Forgot OFF Risk?", "🛡️ Motor Health"
- [ ] AC4: Data chips: "📊 24h Summary", "📈 Fault History", "🔋 Grid Stats", "📡 Status SMS", "🕐 Worker Log"
- [ ] AC5: Tap chip → chip query text sent as user message → AI responds
- [ ] AC6: Purple chips have purple tint border, green chips have green tint border
- [ ] AC7: "📡 Status SMS" chip → triggers actual STATUS SMS send + waits for reply
- [ ] AC8: Chips scroll smoothly with momentum
- [ ] AC9: UI test for chip rendering and tap behavior

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/chat/component/
└── QuickQueryChips.kt

app/src/androidTest/java/com/ksetrasevakah/feature/pumpiq/chat/component/QuickQueryChipsTest.kt
```

## Test Requirements
- All 10 chips visible (scroll to see all)
- Tap "⚡ Power Failure?" → message "Predict power failure" sent
- Tap "📡 Status SMS" → SMS command dispatched
- Purple chips have purple styling, green chips have green styling
