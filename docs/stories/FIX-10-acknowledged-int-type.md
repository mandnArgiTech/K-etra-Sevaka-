# FIX-10: Change SecurityEventEntity.acknowledged from Boolean to Int

**Severity:** 🟡 IMPORTANT  
**Size:** S (< 1h)  
**Dependencies:** None

## Problem
`SecurityEventEntity.acknowledged` is `Boolean` but the DAO query uses `acknowledged = 0` (integer comparison). Room maps Boolean to INTEGER internally, so it *works*, but it's fragile and unclear. If Room's internal mapping ever changes, or if raw SQL is used, this breaks silently.

## Acceptance Criteria
- [ ] AC1: `SecurityEventEntity.acknowledged` changed to `Int` (0 or 1)
- [ ] AC2: `acknowledge()` DAO query sets `acknowledged = 1` (unchanged, already correct)
- [ ] AC3: `getUnacknowledgedHighCount()` query filter `acknowledged = 0` (unchanged, now semantically correct)
- [ ] AC4: Domain model `SecurityEvent` still exposes `acknowledged: Boolean` (mapped in repository)
- [ ] AC5: Default value `= 0` (not acknowledged)
- [ ] AC6: Room schema version bumped with migration (ALTER TABLE not needed — Room handles Int↔Boolean)
- [ ] AC7: Existing tests pass without modification

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/database/entity/SecurityEventEntity.kt
app/src/main/java/com/ksetrasevakah/feature/suraksha/data/repository/SecurityEventRepositoryImpl.kt
```
