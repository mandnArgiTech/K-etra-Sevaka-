# FIX-09: Add Instrumented DAO Tests for All 11 DAOs

**Severity:** 🟡 IMPORTANT  
**Size:** L (4-8h)  
**Dependencies:** None

## Problem
Only 1 instrumented test exists (`MainActivityTest`). Zero DAO tests. All 11 DAOs have complex SQL queries (GROUP BY, HAVING, indices, reactive Flow, upserts) that are untested against real SQLite. Room generates SQL at compile time but query correctness (especially aggregation and filtering) requires runtime verification.

## Acceptance Criteria
- [ ] AC1: Every DAO has at least 3 instrumented tests using in-memory Room DB
- [ ] AC2: `MotorStateDao`: upsert, observe Flow, updateState
- [ ] AC3: `TelemetryDao`: insert, getRecent with time filter, getAvgVoltage
- [ ] AC4: `FaultDao`: insert, getFaultDistribution (GROUP BY), getRecent
- [ ] AC5: `WorkerActivityDao`: upsert, getLast14Days (LIMIT), getByDate, getAvgOnTime
- [ ] AC6: `PredictionCacheDao`: insert, getByType, overwrite on same type
- [ ] AC7: `ChatThreadDao`: insert, getAll sorted, delete
- [ ] AC8: `ChatMessageDao`: insert, getByThread paginated, thread linkage
- [ ] AC9: `BackupLogDao`: insert, getRecent
- [ ] AC10: `SecurityEventDao`: insert, getRecentEvents (Flow), getThreatDistribution (GROUP BY), getHourlyHeatmap, getActivitySpikes, getUnacknowledgedHighCount, acknowledge
- [ ] AC11: `CameraConfigDao`: insert, getByName, updateMode, getAllCameras sorted
- [ ] AC12: `SecurityBriefingDao`: insert, getLatest
- [ ] AC13: All tests use `Room.inMemoryDatabaseBuilder` — no persistent state between tests
- [ ] AC14: `./gradlew connectedAndroidTest` passes (requires emulator/device)
- [ ] AC15: At least 44 instrumented tests total

## Files to Create
```
app/src/androidTest/java/com/ksetrasevakah/core/database/dao/
├── MotorStateDaoTest.kt
├── TelemetryDaoTest.kt
├── FaultDaoTest.kt
├── WorkerActivityDaoTest.kt
├── PredictionCacheDaoTest.kt
├── ChatThreadDaoTest.kt
├── ChatMessageDaoTest.kt
├── BackupLogDaoTest.kt
├── SecurityEventDaoTest.kt
├── CameraConfigDaoTest.kt
└── SecurityBriefingDaoTest.kt
```

## Test Pattern
```kotlin
@RunWith(AndroidJUnit4::class)
class SecurityEventDaoTest {
    private lateinit var db: KsetraDatabase
    private lateinit var dao: SecurityEventDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            KsetraDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.securityEventDao()
    }

    @After
    fun teardown() { db.close() }

    @Test
    fun insertAndRetrieve() { ... }
    @Test
    fun threatDistributionGroupsByLevel() { ... }
    @Test
    fun hourlyHeatmapGroupsByHour() { ... }
}
```
