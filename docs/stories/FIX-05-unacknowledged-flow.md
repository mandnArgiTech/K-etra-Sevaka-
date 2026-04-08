# FIX-05: Change getUnacknowledgedHighCount to Flow<Int>

**Severity:** 🔴 CRITICAL  
**Size:** S (< 1h)  
**Dependencies:** None

## Problem
`SecurityEventDao.getUnacknowledgedHighCount()` is a `suspend fun` returning `Int`. The Hub badge needs to update in real-time as new CRITICAL/HIGH events arrive and as the user acknowledges them. A one-shot query means the badge is stale until the user navigates away and back.

## Acceptance Criteria
- [ ] AC1: `SecurityEventDao.getUnacknowledgedHighCount()` returns `Flow<Int>` (Room reactive query)
- [ ] AC2: `SecurityEventRepository` wraps this as `Flow<Result<Int>>`
- [ ] AC3: Hub ViewModel collects this Flow and updates badge count reactively
- [ ] AC4: New HIGH event inserted → badge count increments without navigation
- [ ] AC5: Event acknowledged → badge count decrements without navigation
- [ ] AC6: Existing unit tests updated to use Turbine for Flow assertion

## Fix
```kotlin
// SecurityEventDao.kt — change from:
@Query("SELECT COUNT(*) FROM security_events WHERE threat_level IN ('HIGH', 'CRITICAL') AND acknowledged = 0")
suspend fun getUnacknowledgedHighCount(): Int

// To:
@Query("SELECT COUNT(*) FROM security_events WHERE threat_level IN ('HIGH', 'CRITICAL') AND acknowledged = 0")
fun getUnacknowledgedHighCount(): Flow<Int>
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/database/dao/SecurityEventDao.kt
app/src/main/java/com/ksetrasevakah/feature/suraksha/data/repository/SecurityEventRepositoryImpl.kt
app/src/main/java/com/ksetrasevakah/feature/suraksha/domain/repository/SecurityEventRepository.kt
app/src/main/java/com/ksetrasevakah/feature/hub/HubViewModel.kt
app/src/test/java/com/ksetrasevakah/feature/suraksha/dashboard/SurakshaDashboardViewModelTest.kt
```

## Test Requirements
- Mock DAO emits Flow(3) → repo emits Result.Success(3)
- Insert new HIGH event → Flow emits incremented count
- Acknowledge event → Flow emits decremented count
