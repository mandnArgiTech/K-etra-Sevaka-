# FIX-14: Replace fallbackToDestructiveMigration with Proper Migration

**Severity:** 🟢 MINOR  
**Size:** M (2-4h)  
**Dependencies:** None

## Problem
`DatabaseModule` uses `fallbackToDestructiveMigration()` — this means upgrading the app from version 1 to 2 **wipes ALL farmer data** instead of migrating it. Room schemas for v1 and v2 exist in `app/schemas/`, proving a migration is expected.

## Acceptance Criteria
- [ ] AC1: `MIGRATION_1_2` object created that ALTERs tables to add Surakṣā tables
- [ ] AC2: `fallbackToDestructiveMigration()` removed from DatabaseModule
- [ ] AC3: `.addMigrations(MIGRATION_1_2)` added to Room builder
- [ ] AC4: Existing PumpIQ data survives the upgrade (verified by test)
- [ ] AC5: Migration tested with `MigrationTestHelper` instrumented test
- [ ] AC6: Room schema JSON files for v1 and v2 are consistent with migration SQL

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/database/di/DatabaseModule.kt
```

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/database/migration/Migrations.kt
app/src/androidTest/java/com/ksetrasevakah/core/database/migration/MigrationTest.kt
```
