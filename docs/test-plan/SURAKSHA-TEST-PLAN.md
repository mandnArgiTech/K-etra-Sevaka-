# Test Plan Addendum — Module 2: Surakṣā (95% Coverage Target)

> Extends the main TEST-PLAN.md. Same frameworks, same coverage targets.

## 1. Surakṣā Unit Tests

| Test Class | Story | Tests | What's Tested |
|-----------|-------|-------|--------------|
| `ThreatLevelTest` | E12-S01 | 5 | Enum properties, isAlertable, isCritical, fromString |
| `CameraModeTest` | E12-S01 | 4 | shouldProcess, shouldAlert, fromString |
| `SecurityEventRepositoryImplTest` | E12-S01 | 5 | Flow emission, error wrapping, threat distribution |
| `CameraConfigRepositoryImplTest` | E12-S01 | 4 | getOrCreate, updateMode, error handling |
| `TapoNotificationParserTest` | E13-S01 | 8 | 3 regex strategies, fallback, event type, empty strings |
| `TapoNotificationListenerTest` | E13-S01 | 8 | Package filter, Notification.when extraction, camera mode routing |
| `ThreatClassifierTest` | E13-S02 | 8 | All time-of-day combos, spike elevation, coordinated intrusion |
| `ActivitySpikeDetectorTest` | E13-S02 | 5 | Spike threshold, window boundary, coordinated detection |
| `ThreatRouterTest` | E13-S02 | 8 | All action types, SILENT override, fallback, vector store |
| `CriticalAlarmManagerTest` | E13-S03 | 5 | Alarm trigger, dismiss, auto-timeout, missing permission |
| `SurakshaDashboardViewModelTest` | E14-S01 | 6 | State mapping, live updates, badge count, empty state |
| `CameraMatrixViewModelTest` | E15-S01 | 4 | Load cameras, mode change, search, bulk action |
| `CrossModuleCorrelatorTest` | E16-S01 | 5 | Temporal match, no match, severity, tampering+PumpIQ |
| `SecurityBriefingGeneratorTest` | E16-S01 | 4 | Events summary, cross-module section, empty events |
| `SystemPromptBuilderTest` (extended) | E16-S01 | 3 | Surakṣā context, correlations, both modules |
| **TOTAL** | | **~82** | |

## 2. Surakṣā DAO Instrumented Tests

| Test Class | Story | Tests |
|-----------|-------|-------|
| `SecurityEventDaoTest` | E12-S01 | 7 |
| `CameraConfigDaoTest` | E12-S01 | 5 |
| `SecurityBriefingDaoTest` | E12-S01 | 3 |
| **TOTAL** | | **~15** |

## 3. Surakṣā Compose UI Tests

| Test Class | Story | Tests |
|-----------|-------|-------|
| `SurakshaDashboardScreenTest` | E14-S01 | 6 |
| `ThreatLedgerItemTest` | E14-S01 | 4 |
| `CriticalAlertOverlayTest` | E14-S02 | 5 |
| `CameraMatrixScreenTest` | E15-S01 | 5 |
| **TOTAL** | | **~20** |

## 4. Surakṣā Integration Tests

| Test Class | Story | Tests |
|-----------|-------|-------|
| `NotificationToLedgerFlowTest` | E18-S01 | 2 |
| `CriticalAlarmFlowTest` | E18-S01 | 2 |
| `ActivitySpikeFlowTest` | E18-S01 | 1 |
| `CrossModuleCorrelationFlowTest` | E18-S01 | 1 |
| `CameraModeFlowTest` | E18-S01 | 2 |
| `CameraAutoDiscoveryFlowTest` | E18-S01 | 1 |
| `SecurityBriefingFlowTest` | E18-S01 | 1 |
| `SurakshaChatFlowTest` | E18-S01 | 1 |
| `SurakshaNavigationFlowTest` | E18-S01 | 1 |
| **TOTAL** | | **~12** |

## 5. Grand Totals (Combined)

| Category | PumpIQ | Surakṣā | Combined |
|----------|--------|---------|----------|
| Unit Tests | ~190 | ~82 | **~272** |
| DAO Tests | ~29 | ~15 | **~44** |
| UI Tests | ~52 | ~20 | **~72** |
| Integration | ~16 | ~12 | **~28** |
| **TOTAL** | **~287** | **~129** | **~416 tests** |

## 6. Surakṣā-Specific Test Utilities

### FakeTapoNotification.kt
```kotlin
object FakeTapoNotification {
    fun create(
        cameraName: String = "Front Gate",
        eventType: String = "Person Detected",
        whenTimestamp: Long = System.currentTimeMillis(),
        packageName: String = "com.tplink.iot"
    ): StatusBarNotification {
        // Build mock SBN with correct packageName, notification.when, extras
    }
}
```

### SurakshaTestDataSeeder.kt
```kotlin
object SurakshaTestDataSeeder {
    suspend fun seed(db: KsetraDatabase) {
        // 5 cameras with configs
        // 50 events across 3 days (30L + 10M + 7H + 3C)
        // 1 cross-module correlation event
    }
}
```
