# E12-S01: Surakṣā Database Entities, DAOs & Repositories

**Epic:** 12 — Surakṣā Data Layer  
**Size:** L (4-8h)  
**Dependencies:** E02-S01 (Room DB exists)

## Description
Add Surakṣā tables to the existing Room database: `security_event`, `camera_config`, `security_briefing`. Create DAOs with indexed queries for Threat Ledger, heatmap, spike detection, and camera management. Create repository interfaces and implementations.

## Acceptance Criteria
- [ ] AC1: `SecurityEventEntity` with all fields: cameraName, eventType, threatLevel, originTimestamp (from `Notification.when`), receiptTimestamp, hourOfDay, isCoordinated, narrative, rawTitle, rawText, dismissed, acknowledged
- [ ] AC2: `CameraConfigEntity` with: cameraName (unique), displayName, mode (ACTIVE/SILENT/DROP), locationTag, firstSeen, lastSeen, totalEvents, iconIndex, notes
- [ ] AC3: `SecurityBriefingEntity` with: briefingType (NIGHTLY/WEEKLY/ON_DEMAND), periodStart, periodEnd, summaryText, totalEvents, criticalCount, highCount, camerasTriggered (JSON), crossModuleRefs (JSON)
- [ ] AC4: `SecurityEventDao.getRecentEvents(since, limit)` returns `Flow<List>` sorted by originTimestamp DESC
- [ ] AC5: `SecurityEventDao.getThreatDistribution(since)` returns counts grouped by threatLevel
- [ ] AC6: `SecurityEventDao.getHourlyHeatmap(since)` returns counts grouped by hourOfDay (0-23)
- [ ] AC7: `SecurityEventDao.getActivitySpikes(windowStart, windowEnd)` returns cameras with ≥3 events in window
- [ ] AC8: `SecurityEventDao.getUnacknowledgedHighCount()` returns `Flow<Int>` for badge
- [ ] AC9: `CameraConfigDao.getOrCreate(name, timestamp)` auto-discovers new cameras, updates existing
- [ ] AC10: `CameraConfigDao.updateMode(id, mode)` changes camera ingestion mode
- [ ] AC11: Database migration from version N to N+1 adds new tables without data loss
- [ ] AC12: Repositories wrap DAOs with `Result<T>` and `Flow<Result<T>>`
- [ ] AC13: All DAO operations pass instrumented tests with in-memory Room DB

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/database/entity/
├── SecurityEventEntity.kt
├── CameraConfigEntity.kt
└── SecurityBriefingEntity.kt

app/src/main/java/com/ksetrasevakah/core/database/dao/
├── SecurityEventDao.kt
├── CameraConfigDao.kt
└── SecurityBriefingDao.kt

app/src/main/java/com/ksetrasevakah/core/database/model/
├── ThreatCount.kt               # data class for distribution query
├── HourlyCount.kt               # data class for heatmap query
└── CameraCount.kt               # data class for spike detection

app/src/main/java/com/ksetrasevakah/feature/suraksha/domain/model/
├── SecurityEvent.kt              # Domain model
├── CameraConfig.kt               # Domain model
├── CameraMode.kt                 # enum: ACTIVE, SILENT, DROP
├── ThreatLevel.kt                # enum: LOW, MEDIUM, HIGH, CRITICAL
├── EventType.kt                  # enum: PERSON, TAMPERING, UNKNOWN
└── SecurityBriefing.kt           # Domain model

app/src/main/java/com/ksetrasevakah/feature/suraksha/domain/repository/
├── SecurityEventRepository.kt
└── CameraConfigRepository.kt

app/src/main/java/com/ksetrasevakah/feature/suraksha/data/repository/
├── SecurityEventRepositoryImpl.kt
└── CameraConfigRepositoryImpl.kt

app/src/main/java/com/ksetrasevakah/feature/suraksha/di/
└── SurakshaModule.kt             # Hilt bindings
```

### Test Files
```
app/src/androidTest/java/com/ksetrasevakah/core/database/dao/
├── SecurityEventDaoTest.kt
├── CameraConfigDaoTest.kt
└── SecurityBriefingDaoTest.kt

app/src/test/java/com/ksetrasevakah/feature/suraksha/data/repository/
├── SecurityEventRepositoryImplTest.kt
└── CameraConfigRepositoryImplTest.kt

app/src/test/java/com/ksetrasevakah/feature/suraksha/domain/model/
├── ThreatLevelTest.kt
└── CameraModeTest.kt
```

## Implementation Details

### ThreatLevel.kt
```kotlin
enum class ThreatLevel {
    LOW, MEDIUM, HIGH, CRITICAL;

    val isAlertable get() = this == HIGH || this == CRITICAL
    val isCritical get() = this == CRITICAL
    
    companion object {
        fun fromString(s: String) = entries.find { it.name == s } ?: MEDIUM
    }
}
```

### CameraMode.kt
```kotlin
enum class CameraMode {
    ACTIVE,  // Full threat router: classify, alert or dismiss
    SILENT,  // Parse & log to DB, but ALWAYS dismiss notification
    DROP;    // Intercept & delete immediately, no processing

    val shouldProcess get() = this != DROP
    val shouldAlert get() = this == ACTIVE
    
    companion object {
        fun fromString(s: String) = entries.find { it.name == s } ?: ACTIVE
    }
}
```

### Room DB Migration
```kotlin
val MIGRATION_N_TO_N1 = object : Migration(N, N + 1) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("""CREATE TABLE IF NOT EXISTS security_event (...)""")
        db.execSQL("""CREATE TABLE IF NOT EXISTS camera_config (...)""")
        db.execSQL("""CREATE TABLE IF NOT EXISTS security_briefing (...)""")
        db.execSQL("""CREATE INDEX IF NOT EXISTS idx_security_event_camera ...""")
        db.execSQL("""CREATE INDEX IF NOT EXISTS idx_security_event_origin ...""")
        db.execSQL("""CREATE INDEX IF NOT EXISTS idx_security_event_threat ...""")
        db.execSQL("""CREATE INDEX IF NOT EXISTS idx_security_event_hour ...""")
        db.execSQL("""CREATE UNIQUE INDEX IF NOT EXISTS idx_camera_config_name ...""")
    }
}
```

## Test Requirements

### SecurityEventDaoTest (Instrumented, in-memory Room)
- Insert event → getRecentEvents returns it
- Insert 5 events → ordered by originTimestamp DESC
- getThreatDistribution → correct grouping (2 LOW + 1 HIGH → {LOW:2, HIGH:1})
- getHourlyHeatmap → correct hour buckets
- getActivitySpikes → returns cameras with ≥3 events in 5 min, ignores those with <3
- getUnacknowledgedHighCount → counts only CRITICAL+HIGH with acknowledged=0
- acknowledge(id) → count decreases by 1

### CameraConfigDaoTest
- getOrCreate with new name → inserts, returns with mode=ACTIVE
- getOrCreate with existing name → updates lastSeen + totalEvents, doesn't duplicate
- updateMode → mode changes, getAllCameras reflects change
- getAllCameras sorted by lastSeen DESC

### SecurityBriefingDaoTest
- Insert briefing → retrieve by type
- Multiple briefings → sorted by periodEnd DESC

### ThreatLevelTest
- LOW.isAlertable = false
- HIGH.isAlertable = true
- CRITICAL.isCritical = true
- fromString("CRITICAL") = CRITICAL
- fromString("garbage") = MEDIUM (fallback)

### CameraModeTest
- DROP.shouldProcess = false
- SILENT.shouldProcess = true, shouldAlert = false
- ACTIVE.shouldProcess = true, shouldAlert = true

### Repository Tests (Unit, mocked DAOs)
- getRecentEvents → Flow emits Result.Success wrapping DAO data
- DAO throws → Flow emits Result.Error
- Camera getOrCreate → delegates to DAO, wraps result

## Definition of Done
- 3 new tables in Room DB with migration
- All 6 DAOs compile with correct queries
- All indices created for performance
- 15+ tests pass (DAO + repo + model)
- KsetraDatabase updated with new entities and DAOs
