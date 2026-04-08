# Test Plan — Kṣetra Sevakaḥ (95% Coverage Target)

## 1. Test Strategy Overview

| Layer | Type | Framework | Coverage Target |
|-------|------|-----------|----------------|
| `core/database/` | Instrumented | Room + JUnit4 | 95% |
| `core/sms/` | Unit + Instrumented | Mockk + Robolectric | 95% |
| `core/ai/` | Unit | Mockk + JUnit5 | 90% (model calls mocked) |
| `core/vectorstore/` | Unit | Mockk + JUnit5 | 90% |
| `core/backup/` | Unit | Mockk + JUnit5 | 95% |
| `core/notification/` | Unit | Mockk + JUnit5 | 90% |
| `core/common/` | Unit | JUnit5 | 100% |
| `feature/*/domain/` | Unit | Mockk + Turbine + JUnit5 | 95% |
| `feature/*/ui/` | UI + Unit | Compose Testing + Mockk | 85% |
| `designsystem/` | Unit + UI | JUnit5 + Compose Testing | 90% |
| `integration/` | Instrumented | Compose + Room in-memory | Full flows |

**Overall target: 95% line coverage on core + domain, 85% on UI**

## 2. Test Categories

### 2.1 Unit Tests (JUnit5 + Mockk + Turbine)
Run with: `./gradlew test`

| Test Class | Story | Tests | What's Tested |
|-----------|-------|-------|--------------|
| `ResultTest` | E01-S01 | 6 | Success/Error/Loading wrapping, getOrNull, getOrThrow |
| `MotorStateTest` | E01-S02 | 8 | Enum properties, display labels, isPending |
| `RiskLevelTest` | E01-S02 | 6 | fromPercentage boundaries, all levels |
| `MotorStateRepositoryImplTest` | E02-S02 | 5 | Flow emission, error wrapping, state mapping |
| `TelemetryRepositoryImplTest` | E02-S02 | 5 | Recent data, avg voltage, error handling |
| `FaultRepositoryImplTest` | E02-S02 | 4 | Distribution query, recent faults |
| `WorkerActivityRepositoryImplTest` | E02-S02 | 5 | 14-day history, avg ON time, forgot count |
| `PredictionRepositoryImplTest` | E02-S02 | 4 | Cache store/retrieve, validity check |
| `ChatRepositoryImplTest` | E02-S02 | 5 | Threads, messages, pagination |
| `SmsCommandSenderTest` | E03-S01 | 5 | Send to correct number, permission check, PendingIntent |
| `SendSmsCommandUseCaseTest` | E03-S01 | 6 | State validation (START when ON = error), pending check |
| `SmsReceiverTest` | E03-S02 | 4 | Filter by number, ignore others, multi-part SMS |
| `SmsParserTest` | E03-S02 | 4 | Valid PDU, null extras, empty array |
| `MotorStateMachineTest` | E03-S03 | 10 | All transitions, invalid transitions, timeout, worker logging |
| `ProcessSmsConfirmationUseCaseTest` | E03-S03 | 5 | MOTOR ON confirm, MOTOR OFF confirm, ALERT handling |
| `ModelManagerTest` | E04-S01 | 5 | Load/unload lifecycle, idle timeout, concurrent requests |
| `MlcLlmEngineTest` | E04-S01 | 4 | Generate with loaded model, auto-load, error |
| `TelemetryParserTest` | E04-S02 | 6 | Valid JSON, missing fields, malformed, fault extraction |
| `IngestionServiceTest` | E04-S02 | 4 | Valid intent, empty body, sequential processing |
| `NarrativeGeneratorTest` | E04-S03 | 4 | Valid telemetry, fault narrative, model failure graceful |
| `PowerFailurePredictorTest` | E07-S01 | 6 | Consistent dip, no dips, partial, insufficient data, boundary |
| `FaultPredictorTest` | E07-S01 | 5 | Dry run cluster, no faults, drift boost, mixed types |
| `WorkerOnTimePredictorTest` | E07-S01 | 5 | Consistent times, high variance, day-specific, insufficient |
| `ForgotOffPredictorTest` | E07-S01 | 6 | With forgots, zero forgots, weekend boost, past-avg, empty |
| `PredictionEngineTest` | E07-S01 | 4 | All predictions run, cache hit, re-compute on new data |
| `ForgotOffWatchdogTest` | E07-S03 | 5 | 45min alert, 75min alert, 105min SMS, no alert, disabled |
| `GetChatThreadsUseCaseTest` | E08-S01 | 3 | List sorted, empty list, error |
| `SendChatMessageUseCaseTest` | E08-S01 | 4 | Add to thread, update lastMessageAt, AI triggered |
| `CreateChatThreadUseCaseTest` | E08-S01 | 3 | Create, auto-title, title truncation |
| `ChatViewModelTest` | E08-S02 | 6 | Send message, AI response, initial query, load thread, typing |
| `ChatOrchestratorTest` | E08-S03 | 5 | Full prompt, empty RAG, model load, error, context |
| `SystemPromptBuilderTest` | E08-S03 | 4 | Motor ON prompt, Motor OFF prompt, predictions, RAG |
| `VectorStoreManagerTest` | E09-S01 | 5 | Add docs, search, persist, reload, concurrent |
| `EmbeddingGeneratorTest` | E09-S01 | 3 | Generate embedding, correct dimension, model error |
| `RagPipelineTest` | E09-S02 | 4 | With matches, no matches, threshold filter, formatted |
| `BackupManagerTest` | E10-S01 | 5 | Create zip, upload, old backup deletion, network fail, log |
| `BackupWorkerTest` | E10-S01 | 3 | Success → Result.success, failure → retry, network → retry |
| `RestoreManagerTest` | E10-S02 | 4 | List backups, download, replace DB, corrupted zip |
| `SettingsViewModelTest` | E11-S01 | 3 | Load settings, toggle watchdog, trigger backup |
| `NotificationManagerTest` | E11-S02 | 4 | Watchdog notification, fault notification, tap intent |
| **TOTAL** | | **~190** | |

### 2.2 Instrumented Tests (Room DAOs — In-Memory DB)
Run with: `./gradlew connectedAndroidTest`

| Test Class | Story | Tests | What's Tested |
|-----------|-------|-------|--------------|
| `MotorStateDaoTest` | E02-S01 | 4 | Insert, update, observe Flow, pending command |
| `TelemetryDaoTest` | E02-S01 | 5 | Insert, getRecent, getAvgVoltage, ordering |
| `FaultDaoTest` | E02-S01 | 4 | Insert, getFaultDistribution grouping, getRecent |
| `WorkerActivityDaoTest` | E02-S01 | 5 | Upsert, getLast14Days, getAvgOnTime, getForgotOffCount |
| `PredictionCacheDaoTest` | E02-S01 | 3 | Insert, retrieve by type, update overwrite |
| `ChatThreadDaoTest` | E02-S01 | 3 | Create, list sorted, delete cascade |
| `ChatMessageDaoTest` | E02-S01 | 3 | Insert, paginate, thread linkage |
| `BackupLogDaoTest` | E02-S01 | 2 | Insert, retrieve recent |
| **TOTAL** | | **~29** | |

### 2.3 Compose UI Tests
Run with: `./gradlew connectedAndroidTest` (or Robolectric for unit-level)

| Test Class | Story | Tests | What's Tested |
|-----------|-------|-------|--------------|
| `MainActivityTest` | E01-S01 | 2 | Activity launches, compose root exists |
| `RiskBadgeTest` | E01-S02 | 3 | Text rendered, correct colors per level |
| `MotorStateIndicatorTest` | E01-S02 | 2 | Labels for ON/OFF states |
| `NavGraphTest` | E05-S01 | 4 | Hub→Dashboard, Dashboard→Chat, back nav, query param |
| `HubScreenTest` | E05-S02 | 4 | PumpIQ tappable, Coming Soon disabled, device info |
| `DashboardScreenTest` | E06-S01 | 4 | Control card visible, predictions visible, charts, summary |
| `MotorControlCardTest` | E06-S02 | 6 | OFF/ON/PENDING states, button text, disabled state, timer |
| `ChartTabBarTest` | E06-S03 | 3 | 5 tabs, selection state, tab switch |
| `PredictionCardsSectionTest` | E07-S02 | 5 | 4 cards visible, tap navigates, risk badges, insufficient data |
| `WorkerPatternChartTest` | E07-S03 | 3 | 14 rows, forgot highlight, today label |
| `PowerForecastChartTest` | E07-S04 | 3 | Lines rendered, danger reference, empty data |
| `ChatScreenTest` | E08-S02 | 6 | Bubbles aligned, send works, typing indicator, thread drawer |
| `QuickQueryChipsTest` | E08-S04 | 4 | 10 chips, tap sends query, purple/green styling |
| `SettingsScreenTest` | E11-S01 | 3 | Panel number, watchdog toggle, backup button |
| **TOTAL** | | **~52** | |

### 2.4 Integration Tests (End-to-End Flows)

| Test Class | Story | Tests | What's Tested |
|-----------|-------|-------|--------------|
| `MotorControlFlowTest` | E11-S03 | 3 | START→PENDING→ON, ON→STOP→OFF, timeout |
| `PredictionFlowTest` | E11-S03 | 4 | All 4 predictions from seeded data, card values |
| `ChatFlowTest` | E11-S03 | 3 | Send message→AI responds, thread saved, prediction query |
| `NavigationFlowTest` | E11-S03 | 2 | Full nav chain forward + back |
| `WatchdogFlowTest` | E11-S03 | 2 | Threshold alert fires, disabled = no alert |
| `WorkerActivityFlowTest` | E11-S03 | 2 | ON transition logs, OFF transition completes record |
| **TOTAL** | | **~16** | |

## 3. Grand Totals

| Category | Test Count |
|----------|-----------|
| Unit Tests | ~190 |
| DAO Instrumented Tests | ~29 |
| Compose UI Tests | ~52 |
| Integration Tests | ~16 |
| **GRAND TOTAL** | **~287 tests** |

## 4. Coverage Measurement

### Gradle Configuration
```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
}

// Use Kover for Kotlin coverage
plugins {
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
}
```

### Commands
```bash
# Unit test coverage
./gradlew koverHtmlReport

# Instrumented test coverage
./gradlew createDebugCoverageReport

# Merged report
./gradlew koverMergedHtmlReport
```

### Coverage Thresholds (enforced in CI)
```kotlin
kover {
    verify {
        rule {
            bound { minValue = 95; metric = LINE; aggregation = COVERED_PERCENTAGE }
            filters { includes { packages("com.ksetrasevakah.core.*") } }
        }
        rule {
            bound { minValue = 95; metric = LINE; aggregation = COVERED_PERCENTAGE }
            filters { includes { packages("com.ksetrasevakah.feature.*.domain.*") } }
        }
        rule {
            bound { minValue = 85; metric = LINE; aggregation = COVERED_PERCENTAGE }
            filters { includes { packages("com.ksetrasevakah.feature.*.ui.*") } }
        }
    }
}
```

## 5. Test Data Strategy

### TestDataSeeder.kt
Reusable across all integration tests:
```kotlin
object TestDataSeeder {
    suspend fun seed14DayHistory(db: KsetraDatabase) {
        // 14 days of worker activity
        //   - Day 4 (Mar 29): forgot_off = true, offTime = 23:48
        //   - Day 9 (Apr 3): forgot_off = true, offTime = 22:15
        //   - All others: normal 06:10–18:50 pattern
        
        // 14 × 24 = 336 telemetry records (hourly)
        //   - Voltage dips at 19:00–22:00 on 11/14 days
        //   - Phase currents: R≈3.8, Y≈3.7, B≈3.9 ± noise
        
        // 28 fault records
        //   - 12 DRY_RUN (13:00–16:00 window)
        //   - 8 OVERLOAD
        //   - 5 PHASE_FAIL  
        //   - 3 LOW_VOLTAGE
        
        // Motor state = OFF
    }
}
```

### FakeSmsManager.kt
```kotlin
class FakeSmsManager : SmsCommandSender {
    val sentMessages = mutableListOf<SmsCommand>()
    var shouldFail = false
    
    override fun sendCommand(command: SmsCommand): Result<Unit> {
        if (shouldFail) return Result.Error("Fake SMS failure")
        sentMessages.add(command)
        return Result.Success(Unit)
    }
    
    fun simulateReply(body: String) { /* trigger BroadcastReceiver */ }
}
```

## 6. Quality Gates

### Per-Story Gate (before moving to next story)
- [ ] All new tests pass: `./gradlew test`
- [ ] No regressions: all previous tests still pass
- [ ] New code covered by tests (check diff coverage)

### Per-Epic Gate
- [ ] Epic's tests pass in isolation
- [ ] Coverage report shows no drops from previous epic
- [ ] Integration point with prior epics tested

### Release Gate
- [ ] All 287 tests pass
- [ ] Coverage: ≥95% core + domain, ≥85% UI
- [ ] No flaky tests (3 consecutive green runs)
- [ ] Integration test suite passes end-to-end
- [ ] Static analysis clean (ktlint, detekt)

## 7. Testing Anti-Patterns to Avoid

1. **No testing AI model output directly** — mock the MLC-LLM engine, test the orchestration/parsing
2. **No testing Android framework internals** — mock SmsManager, BroadcastReceiver, WorkManager
3. **No sleep-based timing** — use Turbine's `awaitItem()`, `advanceTimeBy()` for Flow tests
4. **No shared mutable state between tests** — fresh in-memory DB per test class
5. **No testing Compose rendering pixels** — test semantics, text content, click behavior
