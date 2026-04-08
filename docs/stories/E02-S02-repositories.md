# E02-S02: Repository Interfaces & Implementations

**Epic:** 02 — Data Layer  
**Size:** M (2-4h)  
**Dependencies:** E02-S01

## Description
Create repository interfaces in domain layer and implementations in data layer. Repositories wrap DAOs and expose `Flow<Result<T>>` to ViewModels.

## Acceptance Criteria
- [ ] AC1: `MotorStateRepository` interface + impl expose motor state as `Flow<Result<MotorState>>`
- [ ] AC2: `TelemetryRepository` provides recent telemetry, phase data, voltage averages
- [ ] AC3: `FaultRepository` provides fault distribution and recent faults
- [ ] AC4: `WorkerActivityRepository` provides 14-day history, avg ON time, forgot count
- [ ] AC5: `PredictionRepository` stores/retrieves cached predictions with validity check
- [ ] AC6: `ChatRepository` manages threads and messages with pagination
- [ ] AC7: All repos injected via Hilt `@Binds` module
- [ ] AC8: Every repository method wraps exceptions in `Result.Error`
- [ ] AC9: Unit tests pass for all repository implementations with mocked DAOs

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/domain/repository/
├── MotorStateRepository.kt
├── TelemetryRepository.kt
├── FaultRepository.kt
├── WorkerActivityRepository.kt
├── PredictionRepository.kt
└── ChatRepository.kt

app/src/main/java/com/ksetrasevakah/core/data/repository/
├── MotorStateRepositoryImpl.kt
├── TelemetryRepositoryImpl.kt
├── FaultRepositoryImpl.kt
├── WorkerActivityRepositoryImpl.kt
├── PredictionRepositoryImpl.kt
└── ChatRepositoryImpl.kt

app/src/main/java/com/ksetrasevakah/core/di/RepositoryModule.kt

app/src/test/java/com/ksetrasevakah/core/data/repository/
├── MotorStateRepositoryImplTest.kt
├── TelemetryRepositoryImplTest.kt
├── FaultRepositoryImplTest.kt
├── WorkerActivityRepositoryImplTest.kt
├── PredictionRepositoryImplTest.kt
└── ChatRepositoryImplTest.kt
```

## Implementation Details

### Example Interface
```kotlin
interface MotorStateRepository {
    fun observeMotorState(): Flow<Result<MotorState>>
    suspend fun updateMotorState(state: MotorState): Result<Unit>
    suspend fun setPendingCommand(command: String): Result<Unit>
    suspend fun clearPendingCommand(): Result<Unit>
    suspend fun getSessionDuration(): Result<Long> // millis since currentSessionStart
}
```

### Example Impl Pattern
```kotlin
class MotorStateRepositoryImpl @Inject constructor(
    private val dao: MotorStateDao
) : MotorStateRepository {
    override fun observeMotorState(): Flow<Result<MotorState>> =
        dao.observe().map { entity ->
            try {
                Result.Success(entity?.toMotorState() ?: MotorState.OFF)
            } catch (e: Exception) {
                Result.Error("Failed to read motor state", e)
            }
        }
}
```

## Test Requirements
- Mock each DAO with Mockk
- Verify `Result.Success` wrapping for happy path
- Verify `Result.Error` wrapping when DAO throws
- Verify Flow emission with Turbine

## Definition of Done
- All 6 repos have interface + impl + test
- Hilt `RepositoryModule` binds all
- All tests pass
