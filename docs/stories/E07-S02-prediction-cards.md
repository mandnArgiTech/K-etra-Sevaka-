# E07-S02: Dashboard Prediction Cards UI

**Epic:** 07 — Prediction Engine  
**Size:** M (2-4h)  
**Dependencies:** E07-S01, E06-S01

## Description
Render the 4 AI prediction cards on the dashboard in a 2×2 grid. Each card shows icon, prediction value, risk badge, and is tappable to navigate to AI chat with the corresponding query.

## Acceptance Criteria
- [ ] AC1: "AI Predictions" section header with brain icon and "14-day model" label
- [ ] AC2: 2×2 grid renders 4 cards: Power Failure, Next Fault, Tomorrow ON, Forgot OFF
- [ ] AC3: Power Failure card: red gradient, shows predicted time (e.g., "~19:30"), HIGH risk badge
- [ ] AC4: Next Fault card: amber gradient, shows hours (e.g., "+8h"), MEDIUM risk badge
- [ ] AC5: Tomorrow ON card: green gradient, shows time (e.g., "06:15"), LOW risk badge
- [ ] AC6: Forgot OFF card: purple gradient, shows percentage (e.g., "14%"), risk badge
- [ ] AC7: Each card tappable → navigates to Chat with pre-filled query string
- [ ] AC8: Cards show "No data" gracefully when predictions unavailable (< 3 days history)
- [ ] AC9: Risk badges render correct color per level (green/amber/red/critical)
- [ ] AC10: JetBrains Mono font used for prediction values

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/
├── PredictionCardsSection.kt
└── PredictionCard.kt

app/src/androidTest/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/PredictionCardsSectionTest.kt
```

## Test Requirements
- All 4 cards visible with correct labels
- Power Failure card shows time value from prediction state
- Tap Power Failure card → navigation to chat with "Predict power failure" query
- Tap Tomorrow ON card → navigation to chat with "When will worker turn ON tomorrow" query
- Insufficient data state → "No data" text instead of value
