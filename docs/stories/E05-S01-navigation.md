# E05-S01: Navigation Graph & Screen Routing

**Epic:** 05 — UI Shell  
**Size:** M (2-4h)  
**Dependencies:** E01-S02

## Description
Set up Jetpack Navigation Compose with type-safe routes for all screens: Hub, Dashboard, Chat. Support passing initial query from prediction card taps to chat screen.

## Acceptance Criteria
- [ ] AC1: `NavGraph` composable defines routes: `hub`, `dashboard`, `chat/{initialQuery?}`
- [ ] AC2: `MainActivity` sets NavGraph as content inside `KsetraTheme`
- [ ] AC3: Navigation from Hub → Dashboard works
- [ ] AC4: Navigation from Dashboard → Chat works (with optional query param)
- [ ] AC5: Back navigation works correctly on all screens
- [ ] AC6: Deep link from prediction card → chat with pre-filled query
- [ ] AC7: Compose navigation UI tests pass

## Files to Create
```
app/src/main/java/com/ksetrasevakah/app/
├── navigation/NavGraph.kt
├── navigation/Screen.kt              # Sealed class of screen routes

app/src/androidTest/java/com/ksetrasevakah/app/navigation/NavGraphTest.kt
```

## Implementation Details
### Screen.kt
```kotlin
sealed class Screen(val route: String) {
    data object Hub : Screen("hub")
    data object Dashboard : Screen("dashboard")
    data object Chat : Screen("chat/{initialQuery}") {
        fun createRoute(initialQuery: String? = null) = 
            "chat/${initialQuery ?: ""}"
    }
    data object Settings : Screen("settings")
}
```

## Test Requirements
- Navigate Hub → Dashboard → verify Dashboard displayed
- Navigate Dashboard → Chat("Predict power failure") → verify query arrives
- Back press from Dashboard → Hub displayed
- Back press from Chat → Dashboard displayed
