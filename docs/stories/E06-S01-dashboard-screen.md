# E06-S01: Dashboard Screen Layout & ViewModel

**Epic:** 06 — Dashboard & Motor Control  
**Size:** L (4-8h)  
**Dependencies:** E05-S01, E02-S02, E01-S02

## Description
Build the PumpIQ Dashboard screen: a scrollable layout with motor control card area, prediction cards area, AI summary, and chart tabs. ViewModel collects data from all repositories.

## Acceptance Criteria
- [ ] AC1: `DashboardViewModel` extends `@HiltViewModel` and collects motor state, telemetry, predictions
- [ ] AC2: `DashboardUiState` data class contains: motorState, phaseCurrents, predictions, dailySummary, chartData
- [ ] AC3: Screen scrolls vertically with all sections stacked
- [ ] AC4: ViewModel exposes `StateFlow<DashboardUiState>`
- [ ] AC5: Loading state renders skeleton/shimmer placeholders
- [ ] AC6: Error state renders error card with retry button
- [ ] AC7: Back button navigates to Hub
- [ ] AC8: Chat icon in header navigates to Chat screen
- [ ] AC9: Unit tests for ViewModel state mapping
- [ ] AC10: UI test for screen layout structure

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/pumpiq/dashboard/
├── DashboardScreen.kt
├── DashboardViewModel.kt
├── model/DashboardUiState.kt
└── model/DashboardUiEvent.kt

app/src/test/java/com/ksetrasevakah/feature/pumpiq/dashboard/DashboardViewModelTest.kt
app/src/androidTest/java/com/ksetrasevakah/feature/pumpiq/dashboard/DashboardScreenTest.kt
```

## Test Requirements
### DashboardViewModelTest
- Initial state is Loading
- Motor state flow emission → UiState updated
- Telemetry flow emission → phase currents populated
- Error from repository → UiState.error set

### DashboardScreenTest
- Motor control card visible
- Prediction cards section visible
- AI summary card visible
- Chart tabs visible (Phase Currents, Grid, Faults, Worker Log, Power AI)
