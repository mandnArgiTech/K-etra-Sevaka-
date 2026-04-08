# E02-S01: Room Database — Entities, DAOs, Database, Migrations

**Epic:** 02 — Data Layer  
**Size:** L (4-8h)  
**Dependencies:** E01-S01

## Description
Create the complete Room database with all entities and DAOs needed for PumpIQ: motor state, telemetry, faults, worker activity, predictions, chat threads/messages, and backup log.

## Acceptance Criteria
- [ ] AC1: `KsetraDatabase` abstract class annotated with `@Database` compiles with all entities
- [ ] AC2: All 8 DAOs compile and have correct `@Query`, `@Insert`, `@Update`, `@Delete` annotations
- [ ] AC3: `MotorStateDao.observe()` returns `Flow<MotorStateEntity?>` that emits on change
- [ ] AC4: `TelemetryDao.getRecent(days: Int)` returns telemetry for last N days
- [ ] AC5: `WorkerActivityDao.getLast14Days()` returns sorted worker ON/OFF records
- [ ] AC6: `FaultDao.getFaultDistribution()` returns fault counts grouped by type
- [ ] AC7: `ChatThreadDao` + `ChatMessageDao` support thread listing & message pagination
- [ ] AC8: In-memory Room DB instrumented tests pass for every DAO operation
- [ ] AC9: `PredictionCacheDao` stores/retrieves predictions by type key
- [ ] AC10: All entities have proper `@PrimaryKey`, `@ColumnInfo`, indices

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/database/
├── KsetraDatabase.kt                          # @Database class
├── entity/
│   ├── MotorStateEntity.kt                    # Singleton motor state
│   ├── TelemetryEntity.kt                     # SMS-parsed telemetry records
│   ├── FaultEntity.kt                         # Fault event log
│   ├── WorkerActivityEntity.kt                # Daily ON/OFF timestamps
│   ├── PredictionCacheEntity.kt               # Cached prediction results
│   ├── ChatThreadEntity.kt                    # Chat thread metadata
│   ├── ChatMessageEntity.kt                   # Chat messages
│   └── BackupLogEntity.kt                     # Backup history
├── dao/
│   ├── MotorStateDao.kt
│   ├── TelemetryDao.kt
│   ├── FaultDao.kt
│   ├── WorkerActivityDao.kt
│   ├── PredictionCacheDao.kt
│   ├── ChatThreadDao.kt
│   ├── ChatMessageDao.kt
│   └── BackupLogDao.kt
├── converter/
│   └── Converters.kt                          # TypeConverters for Date, List, etc
└── di/
    └── DatabaseModule.kt                      # Hilt @Module providing DB + DAOs
```

### Test Files
```
app/src/androidTest/java/com/ksetrasevakah/core/database/dao/
├── MotorStateDaoTest.kt
├── TelemetryDaoTest.kt
├── FaultDaoTest.kt
├── WorkerActivityDaoTest.kt
├── PredictionCacheDaoTest.kt
├── ChatThreadDaoTest.kt
├── ChatMessageDaoTest.kt
└── BackupLogDaoTest.kt
```

## Implementation Details

### Key Entities

```kotlin
// MotorStateEntity — singleton, always id=1
@Entity(tableName = "motor_state")
data class MotorStateEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "state") val state: String, // OFF, PENDING_START, ON, PENDING_STOP
    @ColumnInfo(name = "last_on_time") val lastOnTime: Long? = null,
    @ColumnInfo(name = "last_off_time") val lastOffTime: Long? = null,
    @ColumnInfo(name = "current_session_start") val currentSessionStart: Long? = null,
    @ColumnInfo(name = "pending_command") val pendingCommand: String? = null,
    @ColumnInfo(name = "pending_since") val pendingSince: Long? = null,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)

// TelemetryEntity — one per SMS from Taro panel
@Entity(tableName = "telemetry_log", indices = [Index("timestamp")])
data class TelemetryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "raw_sms") val rawSms: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "motor_on") val motorOn: Boolean,
    @ColumnInfo(name = "phase_r") val phaseR: Float? = null,
    @ColumnInfo(name = "phase_y") val phaseY: Float? = null,
    @ColumnInfo(name = "phase_b") val phaseB: Float? = null,
    @ColumnInfo(name = "voltage") val voltage: Float? = null,
    @ColumnInfo(name = "temperature") val temperature: Float? = null,
    @ColumnInfo(name = "runtime_minutes") val runtimeMinutes: Int? = null,
    @ColumnInfo(name = "narrative") val narrative: String? = null // 0.5B generated
)

// WorkerActivityEntity — one per day
@Entity(tableName = "worker_activity", indices = [Index("date", unique = true)])
data class WorkerActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String, // "2025-04-08"
    @ColumnInfo(name = "on_time") val onTime: Long? = null,
    @ColumnInfo(name = "off_time") val offTime: Long? = null,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int? = null,
    @ColumnInfo(name = "forgot_off") val forgotOff: Boolean = false
)

// FaultEntity
@Entity(tableName = "fault_log", indices = [Index("timestamp")])
data class FaultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "fault_type") val faultType: String, // DRY_RUN, OVERLOAD, PHASE_FAIL, LOW_VOLTAGE
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "auto_recovered") val autoRecovered: Boolean = false,
    @ColumnInfo(name = "recovery_time") val recoveryTime: Long? = null
)

// PredictionCacheEntity
@Entity(tableName = "prediction_cache")
data class PredictionCacheEntity(
    @PrimaryKey val type: String, // POWER_FAILURE, NEXT_FAULT, WORKER_NEXT_ON, FORGOT_OFF
    @ColumnInfo(name = "result_json") val resultJson: String,
    @ColumnInfo(name = "confidence") val confidence: Float,
    @ColumnInfo(name = "computed_at") val computedAt: Long,
    @ColumnInfo(name = "valid_until") val validUntil: Long
)
```

### Key DAO Queries

```kotlin
@Dao
interface TelemetryDao {
    @Query("SELECT * FROM telemetry_log WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getRecent(since: Long): Flow<List<TelemetryEntity>>

    @Query("SELECT * FROM telemetry_log WHERE timestamp >= :since ORDER BY timestamp ASC")
    suspend fun getRecentList(since: Long): List<TelemetryEntity>

    @Query("SELECT AVG(voltage) FROM telemetry_log WHERE timestamp >= :since AND timestamp < :until")
    suspend fun getAvgVoltage(since: Long, until: Long): Float?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TelemetryEntity): Long
}

@Dao
interface WorkerActivityDao {
    @Query("SELECT * FROM worker_activity ORDER BY date DESC LIMIT 14")
    fun getLast14Days(): Flow<List<WorkerActivityEntity>>

    @Query("SELECT AVG(on_time) FROM worker_activity WHERE on_time IS NOT NULL ORDER BY date DESC LIMIT :days")
    suspend fun getAvgOnTime(days: Int = 14): Long?

    @Query("SELECT COUNT(*) FROM worker_activity WHERE forgot_off = 1 AND date >= :since")
    suspend fun getForgotOffCount(since: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WorkerActivityEntity)
}

@Dao
interface FaultDao {
    @Query("SELECT fault_type, COUNT(*) as count FROM fault_log WHERE timestamp >= :since GROUP BY fault_type")
    suspend fun getFaultDistribution(since: Long): List<FaultCount>

    @Query("SELECT * FROM fault_log WHERE timestamp >= :since ORDER BY timestamp DESC")
    fun getRecent(since: Long): Flow<List<FaultEntity>>
}

// FaultCount data class for distribution query
data class FaultCount(
    @ColumnInfo(name = "fault_type") val faultType: String,
    @ColumnInfo(name = "count") val count: Int
)
```

### DatabaseModule.kt (Hilt)
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KsetraDatabase =
        Room.databaseBuilder(context, KsetraDatabase::class.java, Constants.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideMotorStateDao(db: KsetraDatabase) = db.motorStateDao()
    @Provides fun provideTelemetryDao(db: KsetraDatabase) = db.telemetryDao()
    @Provides fun provideFaultDao(db: KsetraDatabase) = db.faultDao()
    @Provides fun provideWorkerActivityDao(db: KsetraDatabase) = db.workerActivityDao()
    @Provides fun providePredictionCacheDao(db: KsetraDatabase) = db.predictionCacheDao()
    @Provides fun provideChatThreadDao(db: KsetraDatabase) = db.chatThreadDao()
    @Provides fun provideChatMessageDao(db: KsetraDatabase) = db.chatMessageDao()
    @Provides fun provideBackupLogDao(db: KsetraDatabase) = db.backupLogDao()
}
```

## Test Requirements

### Instrumented Tests (per DAO) — In-Memory Room DB
Each test class creates `Room.inMemoryDatabaseBuilder(...)`:

**MotorStateDaoTest:**
- Insert motor state → observe returns it via Flow
- Update state from OFF → ON → observe emits new value
- pendingCommand stores and retrieves correctly

**TelemetryDaoTest:**
- Insert 5 records → `getRecent(since)` returns correct subset
- `getAvgVoltage` calculates correctly for time range
- Records ordered by timestamp DESC

**FaultDaoTest:**
- Insert faults of different types → `getFaultDistribution` groups correctly
- Count matches inserted records per type

**WorkerActivityDaoTest:**
- `getLast14Days` returns max 14 sorted records
- `getAvgOnTime` calculates average correctly
- `upsert` with same date replaces existing record
- `getForgotOffCount` counts only `forgot_off = true`

**PredictionCacheDaoTest:**
- Insert prediction → retrieve by type key
- Update overwrites previous value for same type
- `validUntil` field stored correctly

**ChatThreadDaoTest + ChatMessageDaoTest:**
- Create thread → list threads returns it
- Add messages to thread → paginate with LIMIT/OFFSET
- Delete thread → cascades to messages

## Definition of Done
- All 8 DAO test classes pass
- Database compiles with all entities registered
- Hilt provides all DAOs correctly
