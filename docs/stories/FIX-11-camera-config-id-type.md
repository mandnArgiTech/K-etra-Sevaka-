# FIX-11: Fix CameraConfig Domain Model id from String to Long

**Severity:** 🟢 MINOR  
**Size:** S (< 30 min)  
**Dependencies:** None

## Problem
`CameraConfig` domain model has `id: String = ""` but `CameraConfigEntity` has `id: Long` with `autoGenerate = true`. The repository mapping between these types will fail or produce incorrect values.

## Acceptance Criteria
- [ ] AC1: `CameraConfig.id` changed to `Long = 0`
- [ ] AC2: All references to `CameraConfig.id` compile correctly
- [ ] AC3: Repository `toEntity()` and `toDomain()` mapping works without type conversion errors
- [ ] AC4: CameraMatrixViewModel passes `Long` id to update operations

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/feature/suraksha/domain/model/CameraConfig.kt
app/src/main/java/com/ksetrasevakah/feature/suraksha/data/repository/CameraConfigRepositoryImpl.kt
app/src/main/java/com/ksetrasevakah/feature/suraksha/prediction/ThreatRouter.kt
```
