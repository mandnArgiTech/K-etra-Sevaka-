# E06-S02: State-Aware Motor Control Card

**Epic:** 06 — Dashboard  
**Size:** M (2-4h)  
**Dependencies:** E06-S01, E03-S03

## Description
The motor control card dynamically changes based on motor state: shows START PUMP when off, STOP PUMP when running, and handles PENDING intermediate states with loading animation.

## Acceptance Criteria
- [ ] AC1: When motor OFF → green "START PUMP" button with play icon
- [ ] AC2: When motor ON → red "STOP PUMP" button with stop icon + session timer + phase currents
- [ ] AC3: When PENDING_START → amber "SENDING SMS..." button (disabled) + pulsing dot + "Waiting for SMS confirmation"
- [ ] AC4: When PENDING_STOP → amber "SENDING SMS..." button (disabled) + pulsing indicator
- [ ] AC5: Tapping START → dispatches `SendSmsCommandUseCase(Start)` → UI enters PENDING
- [ ] AC6: Tapping STOP → dispatches `SendSmsCommandUseCase(Stop)` → UI enters PENDING
- [ ] AC7: Session timer counts up every second when motor ON (HH:MM:SS format)
- [ ] AC8: Phase current mini-cards show R/Y/B values with color coding (red/amber/blue)
- [ ] AC9: PENDING button cannot be tapped (disabled state)
- [ ] AC10: SMS target number "070936 52065" displayed on card

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/
├── MotorControlCard.kt
└── SessionTimer.kt

app/src/androidTest/java/com/ksetrasevakah/feature/pumpiq/dashboard/component/MotorControlCardTest.kt
```

## Test Requirements
- OFF state → "START PUMP" text visible, green styling
- ON state → "STOP PUMP" text visible, red styling, timer counting
- PENDING state → "SENDING SMS..." visible, button disabled
- Tap START → usecase invoked
- Tap STOP → usecase invoked
- Tap PENDING → no action (disabled)
