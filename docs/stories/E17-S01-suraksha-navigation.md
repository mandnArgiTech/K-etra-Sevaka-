# E17-S01: Surakṣā Navigation & Hub Integration

**Epic:** 17 — Surakṣā Integration  
**Size:** M (2-4h)  
**Dependencies:** E14-S01, E15-S01, E05-S01

## Description
Add Surakṣā routes to the NavGraph, activate the module card on the Hub screen with a live unacknowledged-event badge, and wire up all navigation paths.

## Acceptance Criteria
- [ ] AC1: NavGraph extended with routes: `suraksha_dashboard`, `camera_matrix`
- [ ] AC2: Hub screen: Surakṣā module card changes from "Coming Soon" to Active (blue accent)
- [ ] AC3: Hub Surakṣā card shows live badge with unacknowledged HIGH+CRITICAL count
- [ ] AC4: Tap Surakṣā card → navigates to Surakṣā Dashboard
- [ ] AC5: Surakṣā Dashboard header → camera icon → Camera Matrix screen
- [ ] AC6: Surakṣā Dashboard → ledger item tap → Chat with pre-filled query
- [ ] AC7: Critical overlay "VIEW DETAILS" → Chat with event query
- [ ] AC8: Back navigation: Camera Matrix → Dashboard → Hub
- [ ] AC9: Chat screen accessible from both PumpIQ and Surakṣā dashboards

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/app/navigation/NavGraph.kt     ← ADD routes
app/src/main/java/com/ksetrasevakah/app/navigation/Screen.kt       ← ADD screens
app/src/main/java/com/ksetrasevakah/feature/hub/HubScreen.kt       ← ACTIVATE Surakṣā
app/src/main/java/com/ksetrasevakah/feature/hub/HubViewModel.kt    ← ADD badge count
```

## Test Requirements
- Hub → Surakṣā Dashboard navigation works
- Surakṣā Dashboard → Camera Matrix → back works
- Surakṣā card shows badge count from SecurityEventDao
- Ledger item tap → chat with correct query param
