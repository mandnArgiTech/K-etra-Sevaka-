# Module 2: Surakṣā — Security Camera Intelligence Architecture

> Extends the Kṣetra Sevakaḥ platform with TP-Link Tapo camera notification interception, AI-powered threat classification, and cross-module forensic analysis.

---

## 1. System Overview — Surakṣā Integration

```
┌──────────────────────────────────────────────────────────────────────────┐
│                           ANDROID APP                                    │
│                                                                          │
│  ┌────────────────────────────────────────────────────────────────────┐  │
│  │                        SHARED CORE LAYER                           │  │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────────┐   │  │
│  │  │ Room DB  │  │ FAISS    │  │ MLC-LLM  │  │ Google Drive   │   │  │
│  │  │ (shared) │  │ (shared) │  │ 0.5B/3B  │  │ Backup         │   │  │
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────────────────┘   │  │
│  └───────┼──────────────┼─────────────┼─────────────────────────────┘  │
│          │              │             │                                  │
│  ┌───────┴──────────────┴─────────────┴─────────────────────────────┐  │
│  │                     MODULE LAYER                                  │  │
│  │                                                                   │  │
│  │  ┌─────────────────────┐    ┌──────────────────────────────────┐ │  │
│  │  │   MODULE 1: PumpIQ  │    │    MODULE 2: Surakṣā            │ │  │
│  │  │  ┌───────────────┐  │    │  ┌────────────────────────────┐ │ │  │
│  │  │  │ SMS Engine    │  │    │  │ NotificationListener       │ │ │  │
│  │  │  │ Motor Control │  │    │  │ Service                    │ │ │  │
│  │  │  │ Predictions   │  │    │  │ (com.tplink.iot target)    │ │ │  │
│  │  │  └───────────────┘  │    │  └─────────┬──────────────────┘ │ │  │
│  │  │  ┌───────────────┐  │    │            │                    │ │  │
│  │  │  │ Dashboard     │  │    │  ┌─────────▼──────────────────┐ │ │  │
│  │  │  │ AI Chat       │  │    │  │ 0.5B Threat Router         │ │ │  │
│  │  │  └───────────────┘  │    │  │ ┌────────┐ ┌────────────┐ │ │ │  │
│  │  └─────────────────────┘    │  │ │Classify│→│Route/Dismiss│ │ │ │  │
│  │                             │  │ └────────┘ └────────────┘ │ │ │  │
│  │  ┌──────────────────────┐   │  └─────────┬──────────────────┘ │ │  │
│  │  │ CROSS-MODULE CHAT    │   │            │                    │ │  │
│  │  │ (3B Forensic Analyst)│◄──┤  ┌─────────▼──────────────────┐ │ │  │
│  │  │ PumpIQ + Surakṣā     │   │  │ Threat Ledger Dashboard    │ │ │  │
│  │  │ correlation           │   │  │ Camera Matrix Config       │ │ │  │
│  │  └──────────────────────┘   │  │ Critical Alert Overlay     │ │ │  │
│  │                             │  └────────────────────────────┘ │ │  │
│  │                             └──────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                        │
│  Tapo Cameras (11→30+)  ──── TP-Link Tapo App ──── Android Notif. ──┘│
│  (Person/Tamper detect)       (com.tplink.iot)       System            │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. NotificationListenerService — Deep Technical Design

### 2.1 Service Declaration

```xml
<!-- AndroidManifest.xml -->
<service
    android:name=".core.notification.TapoNotificationListener"
    android:label="Kṣetra Sevakaḥ Security"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
    android:exported="false">
    <intent-filter>
        <action android:name="android.service.notification.NotificationListenerService" />
    </intent-filter>
</service>
```

### 2.2 Notification Interception Pipeline

```
Android Notification System
        │
        ▼
┌─────────────────────────────────────────────────┐
│ TapoNotificationListener.onNotificationPosted() │
│                                                  │
│  1. FILTER: sbn.packageName == "com.tplink.iot"  │
│     └── if NO → return (ignore all other apps)   │
│                                                  │
│  2. EXTRACT TIMESTAMP:                           │
│     val originTimestamp = sbn.notification.when   │ ← Camera's timestamp, NOT receipt time
│     // This is the exact moment the camera        │
│     // triggered detection — critical for NVR     │
│     // video scrubbing                            │
│                                                  │
│  3. EXTRACT METADATA:                            │
│     val title = notification.extras              │
│       .getString(Notification.EXTRA_TITLE)        │
│     val text = notification.extras               │
│       .getString(Notification.EXTRA_TEXT)          │
│     val cameraName = parseCameraName(title, text) │
│     val eventType = parseEventType(title, text)   │
│       // "Person Detected" → PERSON               │
│       // "Camera Tampering" → TAMPERING            │
│                                                  │
│  4. CHECK CAMERA CONFIG:                         │
│     val cameraConfig = cameraConfigDao            │
│       .getOrCreate(cameraName)                    │
│     when (cameraConfig.mode) {                   │
│       DROP    → cancelNotification(sbn.key)      │
│                 return                            │
│       SILENT  → cancelNotification(sbn.key)      │
│                 // continue to AI processing      │
│       ACTIVE  → // continue to AI processing      │
│                 // notification fate decided later │
│     }                                            │
│                                                  │
│  5. DISPATCH TO 0.5B THREAT ROUTER               │
│     ThreatRouterService.enqueue(                 │
│       TapoEvent(                                 │
│         cameraName, eventType, originTimestamp,   │
│         rawTitle, rawText, sbn.key, cameraMode   │
│       )                                          │
│     )                                            │
└─────────────────────────────────────────────────┘
```

### 2.3 The `Notification.when` Extraction — Why It Matters

```kotlin
/**
 * CRITICAL DESIGN DECISION:
 * 
 * sbn.postTime         = When Android RECEIVED the notification (unreliable)
 *                        Can be delayed by: Doze mode, network latency, 
 *                        app batching, phone sleep
 * 
 * sbn.notification.when = The origin timestamp set by the Tapo app,
 *                         which maps to the camera's detection moment.
 *                         This is the timestamp the user needs to scrub
 *                         NVR footage to the exact frame.
 *
 * EXTRACTION:
 */
val originTimestamp: Long = sbn.notification.`when`.let { ts ->
    // Tapo app sets `when` to camera detection time
    // Validate: must be within last 5 minutes (guard against epoch 0)
    if (ts > 0 && ts > System.currentTimeMillis() - 300_000) ts
    else sbn.postTime // fallback to receipt time if `when` is invalid
}
```

### 2.4 Camera Name Auto-Discovery

```kotlin
/**
 * Tapo notification format:
 *   Title: "Person Detected"  or  "Camera Tampering Detected"
 *   Text:  "Tapo C200 - Front Gate detected a person" 
 *     or:  "Front Gate: Person detected"
 *     or:  "Camera Tampering detected on Barn Cam"
 *
 * Extraction strategy: regex + fallback
 */
fun parseCameraName(title: String, text: String): String {
    // Strategy 1: "Tapo XXXX - <CameraName> detected..."
    val pattern1 = Regex("""Tapo \w+ - (.+?) detected""")
    pattern1.find(text)?.groupValues?.get(1)?.let { return it.trim() }
    
    // Strategy 2: "<CameraName>: Person detected"
    val pattern2 = Regex("""^(.+?):\s*(Person|Camera)""")
    pattern2.find(text)?.groupValues?.get(1)?.let { return it.trim() }
    
    // Strategy 3: "detected on <CameraName>"
    val pattern3 = Regex("""detected on (.+)$""")
    pattern3.find(text)?.groupValues?.get(1)?.let { return it.trim() }
    
    // Fallback: use raw text hash as identifier
    return "Unknown-${text.hashCode().toUInt().toString(16).take(6)}"
}
```

---

## 3. 0.5B Threat Router — Classification & Routing Engine

### 3.1 Threat Classification Matrix

```
┌──────────────────┬─────────────┬───────────────────────────────────────┐
│ Event Type       │ Time Window │ Threat Level → Action                 │
├──────────────────┼─────────────┼───────────────────────────────────────┤
│ PERSON           │ 06:00–18:00 │ LOW       → Silent ingest, dismiss   │
│ PERSON           │ 18:00–22:00 │ MEDIUM    → Silent ingest, dismiss   │
│ PERSON           │ 22:00–06:00 │ HIGH      → Ingest, KEEP notif.     │
│ TAMPERING        │ Any time    │ CRITICAL  → Ingest, ALARM, overlay   │
│ UNKNOWN          │ Any time    │ MEDIUM    → Ingest, dismiss          │
└──────────────────┴─────────────┴───────────────────────────────────────┘

Additional threat elevation factors:
  - Same camera, 3+ events in 5 minutes → elevate by 1 level
  - Night + multiple cameras triggered within 2 min → CRITICAL (coordinated intrusion)
  - Camera previously in TAMPERING state → next PERSON from same cam = HIGH
```

### 3.2 0.5B Threat Router Prompt

```
You are a farm security threat classifier. Given a camera event, output ONLY valid JSON.

INPUT:
- camera_name: "{camera_name}"
- event_type: "{PERSON|TAMPERING}"
- timestamp: "{ISO-8601}"
- hour_of_day: {0-23}
- recent_events_same_camera_5min: {count}
- recent_events_all_cameras_2min: {count}
- camera_previous_state: "{NORMAL|TAMPERED}"

OUTPUT (JSON only):
{
  "threat_level": "LOW|MEDIUM|HIGH|CRITICAL",
  "threat_reason": "brief reason",
  "narrative": "One sentence describing the event for the security log",
  "is_coordinated": true/false,
  "recommended_action": "DISMISS|NOTIFY|ALARM"
}
```

### 3.3 Routing Actions

```kotlin
sealed class ThreatAction {
    /** Silent ingest: store in DB + vector store, cancel Android notification */
    data class SilentIngest(val event: SecurityEvent) : ThreatAction()
    
    /** Standard notify: store in DB, let Android notification pass through */
    data class StandardNotify(val event: SecurityEvent) : ThreatAction()
    
    /** Critical alarm: store in DB, override DND, play alarm, show overlay */
    data class CriticalAlarm(val event: SecurityEvent) : ThreatAction()
}

// Execution
when (action) {
    is SilentIngest -> {
        securityEventDao.insert(event.toEntity())
        vectorStore.addDocument(event.narrative, event.metadata)
        notificationListener.cancelNotification(event.sbnKey) // ← DISMISS
    }
    is StandardNotify -> {
        securityEventDao.insert(event.toEntity())
        vectorStore.addDocument(event.narrative, event.metadata)
        // Android notification stays visible
    }
    is CriticalAlarm -> {
        securityEventDao.insert(event.toEntity())
        vectorStore.addDocument(event.narrative, event.metadata)
        alarmManager.triggerCriticalAlarm(event) // Override DND
        criticalOverlayState.emit(event) // Full-screen red overlay
        notificationManager.pushHighPriorityNotif(event)
    }
}
```

### 3.4 Notification Cancellation (Anti-Fatigue)

```kotlin
/**
 * For LOW/MEDIUM threats, the app ACTIVELY CANCELS the Tapo notification
 * to prevent the farmer from being bombarded with 200+ daytime person alerts.
 *
 * NotificationListenerService has the power to do this:
 */
fun dismissTapoNotification(sbnKey: String) {
    cancelNotification(sbnKey)
    // The notification vanishes from the shade as if it never existed.
    // But we've already ingested the data into our DB.
    // The farmer sees ZERO noise. The AI sees EVERYTHING.
}
```

---

## 4. Database Schema — Surakṣā Tables

### 4.1 Entity: `security_event`

```sql
CREATE TABLE security_event (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    camera_name     TEXT NOT NULL,                   -- "Front Gate", "Barn Cam"
    event_type      TEXT NOT NULL,                   -- PERSON, TAMPERING
    threat_level    TEXT NOT NULL,                   -- LOW, MEDIUM, HIGH, CRITICAL
    threat_reason   TEXT,                            -- "Nighttime person detection"
    origin_timestamp INTEGER NOT NULL,               -- Notification.when (camera time)
    receipt_timestamp INTEGER NOT NULL,              -- System.currentTimeMillis()
    hour_of_day     INTEGER NOT NULL,                -- 0-23, extracted for fast queries
    is_coordinated  INTEGER NOT NULL DEFAULT 0,      -- 1 if part of multi-camera event
    narrative       TEXT,                            -- 0.5B generated narrative
    raw_title       TEXT NOT NULL,                   -- Original notification title
    raw_text        TEXT NOT NULL,                   -- Original notification text
    dismissed       INTEGER NOT NULL DEFAULT 0,      -- 1 if notification was cancelled
    acknowledged    INTEGER NOT NULL DEFAULT 0,      -- 1 if user tapped/viewed
    created_at      INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)
);

CREATE INDEX idx_security_event_camera ON security_event(camera_name);
CREATE INDEX idx_security_event_origin ON security_event(origin_timestamp);
CREATE INDEX idx_security_event_threat ON security_event(threat_level);
CREATE INDEX idx_security_event_hour ON security_event(hour_of_day);
```

### 4.2 Entity: `camera_config`

```sql
CREATE TABLE camera_config (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    camera_name     TEXT NOT NULL UNIQUE,            -- Auto-discovered from notifications
    display_name    TEXT,                            -- User-assigned friendly name
    mode            TEXT NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE, SILENT, DROP
    location_tag    TEXT,                            -- "Main Gate", "Perimeter", "Barn"
    first_seen      INTEGER NOT NULL,                -- Timestamp of first notification
    last_seen       INTEGER NOT NULL,                -- Timestamp of most recent notification
    total_events    INTEGER NOT NULL DEFAULT 0,      -- Running count
    icon_index      INTEGER NOT NULL DEFAULT 0,      -- UI icon selection (0-5)
    notes           TEXT,                            -- User notes about this camera
    created_at      INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000),
    updated_at      INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)
);

CREATE UNIQUE INDEX idx_camera_config_name ON camera_config(camera_name);
```

### 4.3 Entity: `security_briefing`

```sql
CREATE TABLE security_briefing (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    briefing_type   TEXT NOT NULL,                   -- NIGHTLY, WEEKLY, ON_DEMAND
    period_start    INTEGER NOT NULL,                -- Start of covered period
    period_end      INTEGER NOT NULL,                -- End of covered period
    summary_text    TEXT NOT NULL,                   -- 3B model generated briefing
    total_events    INTEGER NOT NULL,
    critical_count  INTEGER NOT NULL DEFAULT 0,
    high_count      INTEGER NOT NULL DEFAULT 0,
    cameras_triggered TEXT,                          -- JSON array of camera names
    cross_module_refs TEXT,                          -- JSON: PumpIQ correlations found
    created_at      INTEGER NOT NULL DEFAULT (strftime('%s','now') * 1000)
);
```

### 4.4 Key DAO Queries

```kotlin
@Dao
interface SecurityEventDao {
    /** Threat Ledger: recent events sorted by camera timestamp */
    @Query("""
        SELECT * FROM security_event 
        WHERE origin_timestamp >= :since 
        ORDER BY origin_timestamp DESC 
        LIMIT :limit
    """)
    fun getRecentEvents(since: Long, limit: Int = 100): Flow<List<SecurityEventEntity>>

    /** Events by threat level for dashboard cards */
    @Query("""
        SELECT threat_level, COUNT(*) as count 
        FROM security_event 
        WHERE origin_timestamp >= :since 
        GROUP BY threat_level
    """)
    suspend fun getThreatDistribution(since: Long): List<ThreatCount>

    /** Hourly heatmap data: events per hour across all cameras */
    @Query("""
        SELECT hour_of_day, COUNT(*) as count 
        FROM security_event 
        WHERE origin_timestamp >= :since 
        GROUP BY hour_of_day 
        ORDER BY hour_of_day
    """)
    suspend fun getHourlyHeatmap(since: Long): List<HourlyCount>

    /** Activity spike detection: >3 events from same camera in 5 min window */
    @Query("""
        SELECT camera_name, COUNT(*) as count 
        FROM security_event 
        WHERE origin_timestamp >= :windowStart 
          AND origin_timestamp <= :windowEnd 
        GROUP BY camera_name 
        HAVING count >= 3
    """)
    suspend fun getActivitySpikes(windowStart: Long, windowEnd: Long): List<CameraCount>

    /** Events for a specific camera (Camera detail view) */
    @Query("""
        SELECT * FROM security_event 
        WHERE camera_name = :cameraName 
        ORDER BY origin_timestamp DESC 
        LIMIT :limit
    """)
    fun getEventsForCamera(cameraName: String, limit: Int = 50): Flow<List<SecurityEventEntity>>

    /** Un-acknowledged critical/high events (for badge count) */
    @Query("""
        SELECT COUNT(*) FROM security_event 
        WHERE threat_level IN ('CRITICAL', 'HIGH') 
          AND acknowledged = 0
    """)
    fun getUnacknowledgedHighCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: SecurityEventEntity): Long
    
    @Query("UPDATE security_event SET acknowledged = 1 WHERE id = :eventId")
    suspend fun acknowledge(eventId: Long)
}

@Dao
interface CameraConfigDao {
    @Query("SELECT * FROM camera_config ORDER BY last_seen DESC")
    fun getAllCameras(): Flow<List<CameraConfigEntity>>

    @Query("SELECT * FROM camera_config WHERE camera_name = :name LIMIT 1")
    suspend fun getByName(name: String): CameraConfigEntity?

    /** Auto-discover: insert if new, update last_seen if existing */
    @Transaction
    suspend fun getOrCreate(name: String, timestamp: Long): CameraConfigEntity {
        val existing = getByName(name)
        if (existing != null) {
            updateLastSeen(existing.id, timestamp, existing.totalEvents + 1)
            return existing.copy(lastSeen = timestamp, totalEvents = existing.totalEvents + 1)
        }
        val entity = CameraConfigEntity(
            cameraName = name, mode = "ACTIVE",
            firstSeen = timestamp, lastSeen = timestamp, totalEvents = 1
        )
        val id = insert(entity)
        return entity.copy(id = id)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: CameraConfigEntity): Long

    @Query("UPDATE camera_config SET mode = :mode, updated_at = :now WHERE id = :id")
    suspend fun updateMode(id: Long, mode: String, now: Long = System.currentTimeMillis())

    @Query("""
        UPDATE camera_config 
        SET last_seen = :timestamp, total_events = :count, updated_at = :timestamp 
        WHERE id = :id
    """)
    suspend fun updateLastSeen(id: Long, timestamp: Long, count: Int)
}
```

---

## 5. 3B Forensic Analyst — Cross-Module Intelligence

### 5.1 Security Briefing Generation

```
Triggered: Every day at 06:00 AM (nightly briefing) + on-demand

System Prompt for 3B model:
"""
You are the Surakṣā Forensic Analyst for a farm security system.
Generate a Security Briefing covering the period {start} to {end}.

SECURITY EVENTS:
{events_json}

CAMERA INVENTORY:
{camera_configs_json}

PUMPIQ MOTOR LOG (for cross-referencing):
{motor_events_same_period}

POWER OUTAGE LOG:
{power_outages_same_period}

Generate a briefing with:
1. SUMMARY: One paragraph overview
2. CRITICAL EVENTS: List any CRITICAL/HIGH events with exact timestamps
3. PATTERNS: Any repeated activity, time clustering, or camera patterns
4. CROSS-MODULE CORRELATIONS: Did any security event coincide with 
   a power failure or motor state change? (e.g., "Person detected at 
   transformer camera at 22:14 — power failure recorded at 22:16")
5. RECOMMENDATIONS: Actionable items for the farmer

Format as structured text with clear section headers.
"""
```

### 5.2 Cross-Module Correlation Engine

```kotlin
/**
 * Finds temporal correlations between Surakṣā and PumpIQ events.
 * 
 * Correlation window: ±5 minutes
 * 
 * Example discoveries:
 * - Person @ transformer cam + power outage within 5 min → THEFT risk
 * - Person @ pump house + motor started at odd hour → unauthorized usage
 * - Multiple camera tampering + motor stopped → coordinated sabotage
 */
class CrossModuleCorrelator @Inject constructor(
    private val securityEventDao: SecurityEventDao,
    private val telemetryDao: TelemetryDao,
    private val faultDao: FaultDao,
    private val workerActivityDao: WorkerActivityDao,
) {
    data class Correlation(
        val securityEvent: SecurityEventEntity,
        val pumpIqEvent: String, // Description of correlated PumpIQ event
        val timeDeltaSeconds: Long,
        val severity: RiskLevel,
        val description: String
    )

    suspend fun findCorrelations(
        since: Long,
        windowMs: Long = 300_000 // 5 minutes
    ): List<Correlation> { /* ... */ }
}
```

---

## 6. Critical Alert System

### 6.1 Critical Alarm Override

```kotlin
/**
 * When TAMPERING is detected at ANY time, or coordinated intrusion detected:
 * 1. Override Do Not Disturb
 * 2. Play loud alarm sound (custom WAV, 15 seconds)
 * 3. Vibrate aggressively
 * 4. Push full-screen red overlay on the phone
 * 5. High-priority notification that bypasses all filtering
 */
class CriticalAlarmManager @Inject constructor(
    private val context: Context,
    private val audioManager: AudioManager,
    private val notificationManager: NotificationManagerCompat,
) {
    fun triggerCriticalAlarm(event: SecurityEvent) {
        // 1. Request DND override (requires NOTIFICATION_POLICY_ACCESS permission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nm = context.getSystemService(NotificationManager::class.java)
            if (nm.isNotificationPolicyAccessGranted) {
                nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
        }
        
        // 2. Play alarm via MediaPlayer at max volume
        val previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM),
            0
        )
        // MediaPlayer plays R.raw.critical_alarm (15-second alert tone)
        
        // 3. Vibrate pattern: urgent bursts
        val vibrator = context.getSystemService(Vibrator::class.java)
        vibrator?.vibrate(VibrationEffect.createWaveform(
            longArrayOf(0, 500, 200, 500, 200, 1000), -1
        ))
        
        // 4. Emit to CriticalOverlayState (observed by UI layer)
        criticalOverlayState.value = CriticalOverlayData(
            cameraName = event.cameraName,
            eventType = event.eventType,
            timestamp = event.originTimestamp,
            isActive = true
        )
        
        // 5. Full-screen intent notification
        val fullScreenIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("critical_event_id", event.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        // Build notification with CATEGORY_ALARM, PRIORITY_MAX, fullScreenIntent
    }
}
```

---

## 7. Package Structure Extension

```
com.ksetrasevakah/
├── core/
│   ├── database/
│   │   ├── entity/
│   │   │   ├── SecurityEventEntity.kt        ← NEW
│   │   │   ├── CameraConfigEntity.kt         ← NEW
│   │   │   └── SecurityBriefingEntity.kt     ← NEW
│   │   ├── dao/
│   │   │   ├── SecurityEventDao.kt           ← NEW
│   │   │   ├── CameraConfigDao.kt            ← NEW
│   │   │   └── SecurityBriefingDao.kt        ← NEW
│   │   └── KsetraDatabase.kt                 ← MODIFY (add new entities + DAOs)
│   ├── notification/
│   │   ├── TapoNotificationListener.kt       ← NEW (NotificationListenerService)
│   │   ├── TapoNotificationParser.kt         ← NEW
│   │   └── CriticalAlarmManager.kt           ← NEW
│   └── ai/
│       └── prompt/
│           ├── ThreatRouterPrompt.kt          ← NEW
│           └── SecurityBriefingPrompt.kt      ← NEW
├── feature/
│   ├── suraksha/                              ← NEW MODULE
│   │   ├── dashboard/
│   │   │   ├── SurakshaDashboardScreen.kt
│   │   │   ├── SurakshaDashboardViewModel.kt
│   │   │   ├── model/SurakshaDashboardUiState.kt
│   │   │   └── component/
│   │   │       ├── ThreatLedger.kt
│   │   │       ├── ThreatLedgerItem.kt
│   │   │       ├── SecurityBriefingCard.kt
│   │   │       ├── ThreatHeatmap.kt
│   │   │       ├── CriticalAlertOverlay.kt
│   │   │       └── ThreatSummaryCards.kt
│   │   ├── camera/
│   │   │   ├── CameraMatrixScreen.kt
│   │   │   ├── CameraMatrixViewModel.kt
│   │   │   └── component/
│   │   │       ├── CameraConfigRow.kt
│   │   │       └── CameraModeSelector.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── SecurityEvent.kt
│   │   │   │   ├── CameraConfig.kt
│   │   │   │   ├── CameraMode.kt
│   │   │   │   ├── ThreatLevel.kt
│   │   │   │   ├── SecurityBriefing.kt
│   │   │   │   └── CriticalOverlayData.kt
│   │   │   ├── usecase/
│   │   │   │   ├── ClassifyThreatUseCase.kt
│   │   │   │   ├── GetThreatLedgerUseCase.kt
│   │   │   │   ├── GetSecurityBriefingUseCase.kt
│   │   │   │   ├── UpdateCameraModeUseCase.kt
│   │   │   │   ├── AcknowledgeEventUseCase.kt
│   │   │   │   └── GetCrossModuleCorrelationsUseCase.kt
│   │   │   └── repository/
│   │   │       ├── SecurityEventRepository.kt
│   │   │       └── CameraConfigRepository.kt
│   │   └── prediction/
│   │       ├── ThreatRouter.kt
│   │       ├── CrossModuleCorrelator.kt
│   │       └── SecurityBriefingGenerator.kt
│   └── chat/                                  ← MODIFY (extend for cross-module)
│       └── prompt/
│           └── SystemPromptBuilder.kt         ← MODIFY (add Surakṣā context)
```

---

## 8. Surakṣā-Specific Design Tokens

```json
{
  "suraksha": {
    "colors": {
      "shieldBlue": "#3B82F6",
      "shieldBlueDim": "#1E3A5F",
      "shieldBlueGlow": "rgba(59,130,246,0.15)",
      "alertCriticalBg": "#991B1B",
      "alertCriticalBorder": "#DC2626",
      "alertCriticalPulse": "#FCA5A5",
      "personGreen": "#22C55E",
      "tamperingRed": "#EF4444",
      "nightPurple": "#7C3AED",
      "dayAmber": "#F59E0B",
      "silentGray": "#6B7280",
      "dropDark": "#374151"
    },
    "threatLedgerColors": {
      "LOW": "#4ADE80",
      "MEDIUM": "#F59E0B",
      "HIGH": "#EF4444",
      "CRITICAL": "#DC2626"
    },
    "cameraModeColors": {
      "ACTIVE": "#3B82F6",
      "SILENT": "#6B7280",
      "DROP": "#374151"
    }
  }
}
```
