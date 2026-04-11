# WIRE-02: Normalized Event Model + DB Schema Alignment

**Severity:** 🔴 CRITICAL  
**Size:** L (4-8h)  
**Dependencies:** WIRE-01

## Problem
The current DB schema doesn't match real Taro data:

1. `TelemetryEntity` stores `phaseR/Y/B` as **current** (Amps) — but real Taro SMS sends **voltage** R/Y/B for Low Voltage/Phase Failure events, and **current** R/Y/B only for Dryrun/Overload. These are different physical measurements stored in the same columns.
2. No `event_type` column — can't distinguish "Motor ON" from "Low Voltage" from "Dryrun". Everything is a flat telemetry row.
3. No `source` field (Keypad vs APP) — critical for worker behavior analysis.
4. No `power_state` tracking — Device Powered ON / Power Failure / Power Resumed form a separate state machine.
5. `FaultEntity` is disconnected from `TelemetryEntity` — faults aren't extracted during ingestion.
6. `WorkerActivityEntity` only tracks ON/OFF times — doesn't track via-source or multiple ON/OFF cycles per day.

## Solution: Unified Event Table + Separate Structured Tables

### Architecture Decision
The `telemetry_log` table becomes a **unified event log** — every SMS is one row. Separate tables hold structured lookups derived from these events.

```
SMS arrives
    ↓
TaroSmsParser → TaroPanelEvent (sealed class)
    ↓
┌─────────────────────────────────────────────────┐
│ SmsTelemetryProcessor.process(event)            │
│                                                 │
│  1. INSERT into panel_event (unified event log) │
│  2. IF MotorOn/MotorOff → UPDATE motor_state    │
│     + INSERT worker_activity_log                │
│  3. IF fault (Dryrun/PhaseFailure/Overload/     │
│     LowVoltage) → INSERT fault_log              │
│  4. IF PowerFailure/PowerResumed →              │
│     UPDATE power_state                          │
│  5. Generate narrative → embed into vector DB   │
└─────────────────────────────────────────────────┘
```

## Acceptance Criteria
- [ ] AC1: New `PanelEventEntity` replaces `TelemetryEntity` as the primary event log
- [ ] AC2: `PanelEventEntity` has: id, timestamp, eventType (enum string), severity, source (KEYPAD/APP/null), voltageR/Y/B (nullable), currentR/Y/B (nullable), motorStatus (nullable bool), mode (nullable), rawSms, narrative
- [ ] AC3: `eventType` column stores exact type: `MOTOR_ON`, `MOTOR_OFF`, `POWER_FAILURE`, `POWER_RESUMED`, `DEVICE_POWERED_ON`, `LOW_VOLTAGE`, `DRYRUN`, `PHASE_FAILURE`, `OVERLOAD`, `COMMAND_NOT_MATCHED`, `UNKNOWN`
- [ ] AC4: `MotorStateEntity` updated by MotorOn/MotorOff events — `state` reflects real motor status
- [ ] AC5: `FaultEntity` auto-created for: DRYRUN, PHASE_FAILURE, OVERLOAD, LOW_VOLTAGE events
- [ ] AC6: `WorkerActivityEntity` enhanced: tracks multiple ON/OFF cycles per day, source per event
- [ ] AC7: New `PowerStateEntity` (singleton like MotorState): tracks grid power ON/OFF state
- [ ] AC8: DAO queries updated for new schema
- [ ] AC9: Room migration from current schema to new schema
- [ ] AC10: Old `TelemetryEntity` data preserved during migration (mapped to panel_event)

## New Entity: PanelEventEntity

```kotlin
@Entity(
    tableName = "panel_event",
    indices = [
        Index("timestamp"),
        Index("event_type"),
        Index("event_type", "timestamp")  // Composite for type+time queries
    ]
)
data class PanelEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "event_type") val eventType: String,     // MOTOR_ON, DRYRUN, etc.
    @ColumnInfo(name = "severity") val severity: String,        // ATTENTION, ALERT
    @ColumnInfo(name = "source") val source: String? = null,    // KEYPAD, APP, null
    @ColumnInfo(name = "voltage_r") val voltageR: Float? = null,
    @ColumnInfo(name = "voltage_y") val voltageY: Float? = null,
    @ColumnInfo(name = "voltage_b") val voltageB: Float? = null,
    @ColumnInfo(name = "current_r") val currentR: Float? = null,
    @ColumnInfo(name = "current_y") val currentY: Float? = null,
    @ColumnInfo(name = "current_b") val currentB: Float? = null,
    @ColumnInfo(name = "motor_status") val motorStatus: Boolean? = null,
    @ColumnInfo(name = "panel_mode") val panelMode: String? = null,  // Manual, Auto
    @ColumnInfo(name = "failed_phases") val failedPhases: String? = null,  // "R/B", "Y"
    @ColumnInfo(name = "raw_sms") val rawSms: String,
    @ColumnInfo(name = "narrative") val narrative: String? = null
)
```

## New Entity: PowerStateEntity

```kotlin
@Entity(tableName = "power_state")
data class PowerStateEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "is_on") val isOn: Boolean = true,
    @ColumnInfo(name = "last_failure_time") val lastFailureTime: Long? = null,
    @ColumnInfo(name = "last_resume_time") val lastResumeTime: Long? = null,
    @ColumnInfo(name = "failure_count_today") val failureCountToday: Int = 0,
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
```

## Enhanced WorkerActivityEntity

```kotlin
@Entity(tableName = "worker_activity", indices = [Index("date")])
data class WorkerActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String,           // "2026-04-11"
    @ColumnInfo(name = "on_time") val onTime: Long? = null,
    @ColumnInfo(name = "off_time") val offTime: Long? = null,
    @ColumnInfo(name = "on_source") val onSource: String? = null,   // KEYPAD, APP
    @ColumnInfo(name = "off_source") val offSource: String? = null,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int? = null,
    @ColumnInfo(name = "forgot_off") val forgotOff: Boolean = false,
    @ColumnInfo(name = "cycle_number") val cycleNumber: Int = 1     // Multiple ON/OFF per day
)
```

## Key DAO Queries (PanelEventDao)

```kotlin
@Dao
interface PanelEventDao {
    @Query("SELECT * FROM panel_event ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEvents(limit: Int = 100): Flow<List<PanelEventEntity>>

    @Query("SELECT * FROM panel_event WHERE event_type = :type ORDER BY timestamp DESC LIMIT :limit")
    fun getEventsByType(type: String, limit: Int = 50): Flow<List<PanelEventEntity>>

    @Query("SELECT * FROM panel_event WHERE event_type IN ('MOTOR_ON','MOTOR_OFF') ORDER BY timestamp DESC LIMIT :limit")
    fun getMotorEvents(limit: Int = 50): Flow<List<PanelEventEntity>>

    @Query("SELECT * FROM panel_event WHERE event_type IN ('POWER_FAILURE','POWER_RESUMED','DEVICE_POWERED_ON') ORDER BY timestamp DESC LIMIT :limit")
    fun getPowerEvents(limit: Int = 50): Flow<List<PanelEventEntity>>

    @Query("SELECT * FROM panel_event WHERE event_type IN ('DRYRUN','PHASE_FAILURE','OVERLOAD','LOW_VOLTAGE') ORDER BY timestamp DESC LIMIT :limit")
    fun getFaultEvents(limit: Int = 50): Flow<List<PanelEventEntity>>

    /** Hourly event count for heatmap — which hours have most activity */
    @Query("""
        SELECT CAST(strftime('%H', datetime(timestamp/1000, 'unixepoch', 'localtime')) AS INTEGER) as hour,
               COUNT(*) as count
        FROM panel_event
        WHERE timestamp >= :since
        GROUP BY hour ORDER BY hour
    """)
    suspend fun getHourlyDistribution(since: Long): List<HourlyCount>

    /** Event type distribution for pie chart */
    @Query("SELECT event_type as type, COUNT(*) as count FROM panel_event WHERE timestamp >= :since GROUP BY event_type")
    suspend fun getEventTypeDistribution(since: Long): List<EventTypeCount>

    /** Voltage readings over time for voltage chart */
    @Query("SELECT timestamp, voltage_r, voltage_y, voltage_b FROM panel_event WHERE voltage_r IS NOT NULL AND timestamp >= :since ORDER BY timestamp")
    suspend fun getVoltageReadings(since: Long): List<VoltageReading>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: PanelEventEntity): Long
}

data class EventTypeCount(
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "count") val count: Int
)

data class VoltageReading(
    val timestamp: Long,
    @ColumnInfo(name = "voltage_r") val voltageR: Float?,
    @ColumnInfo(name = "voltage_y") val voltageY: Float?,
    @ColumnInfo(name = "voltage_b") val voltageB: Float?
)
```

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/database/entity/PanelEventEntity.kt
app/src/main/java/com/ksetrasevakah/core/database/entity/PowerStateEntity.kt
app/src/main/java/com/ksetrasevakah/core/database/dao/PanelEventDao.kt
app/src/main/java/com/ksetrasevakah/core/database/dao/PowerStateDao.kt
app/src/main/java/com/ksetrasevakah/core/database/model/EventTypeCount.kt
app/src/main/java/com/ksetrasevakah/core/database/model/VoltageReading.kt
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/database/KsetraDatabase.kt        ← add new entities/DAOs
app/src/main/java/com/ksetrasevakah/core/database/entity/WorkerActivityEntity.kt ← add source, cycle
app/src/main/java/com/ksetrasevakah/core/database/di/DatabaseModule.kt     ← provide new DAOs
app/src/main/java/com/ksetrasevakah/core/database/migration/Migrations.kt  ← new migration
```

## Test Requirements
- PanelEventDao: insert 5 different event types → getRecentEvents returns all
- PanelEventDao: getEventsByType("MOTOR_ON") → only motor events
- PanelEventDao: getHourlyDistribution → correct hour grouping
- PanelEventDao: getVoltageReadings → returns rows with voltage data
- PowerStateDao: update power off → update power on → tracks timestamps
- WorkerActivity with cycle_number: 2 ON/OFF cycles in same day → both preserved

## Definition of Done
- Schema matches real Taro data exactly
- No data type confusion (voltage vs current)
- Every event type has a home in the schema
- Migration preserves existing data
