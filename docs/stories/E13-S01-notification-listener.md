# E13-S01: NotificationListenerService for Tapo Camera Interception

**Epic:** 13 — Surakṣā Notification Engine  
**Size:** L (4-8h)  
**Dependencies:** E12-S01

## Description
Implement the `NotificationListenerService` that intercepts all TP-Link Tapo app notifications, extracts the `Notification.when` origin timestamp (camera detection time), parses camera name and event type, and checks camera config mode before dispatching to the Threat Router.

## Acceptance Criteria
- [ ] AC1: `TapoNotificationListener` extends `NotificationListenerService`
- [ ] AC2: `onNotificationPosted` filters ONLY `sbn.packageName == "com.tplink.iot"` — all other packages ignored
- [ ] AC3: Extracts `sbn.notification.when` as originTimestamp (NOT `sbn.postTime`)
- [ ] AC4: Validates originTimestamp: if 0 or >5 min old, falls back to `sbn.postTime`
- [ ] AC5: `TapoNotificationParser.parseCameraName(title, text)` extracts camera name via 3 regex strategies + fallback
- [ ] AC6: `TapoNotificationParser.parseEventType(title, text)` returns PERSON, TAMPERING, or UNKNOWN
- [ ] AC7: Auto-discovers new cameras via `CameraConfigDao.getOrCreate()`
- [ ] AC8: Camera mode = DROP → `cancelNotification(sbn.key)` immediately, no further processing
- [ ] AC9: Camera mode = SILENT or ACTIVE → dispatches to `ThreatRouterService` via Intent
- [ ] AC10: Service declared in AndroidManifest with correct permission
- [ ] AC11: Handles rapid notification bursts (10+ notifications/second) without ANR
- [ ] AC12: Unit tests for parser, integration test for listener behavior

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/notification/
├── TapoNotificationListener.kt          # NotificationListenerService
├── TapoNotificationParser.kt            # Camera name + event type extraction
└── model/TapoEvent.kt                   # Data class passed to ThreatRouter

app/src/test/java/com/ksetrasevakah/core/notification/
├── TapoNotificationParserTest.kt
└── TapoNotificationListenerTest.kt
```

## Implementation Details

### TapoEvent.kt
```kotlin
data class TapoEvent(
    val cameraName: String,
    val eventType: EventType,              // PERSON, TAMPERING, UNKNOWN
    val originTimestamp: Long,             // From Notification.when
    val receiptTimestamp: Long,            // System.currentTimeMillis()
    val rawTitle: String,
    val rawText: String,
    val sbnKey: String,                    // For cancelNotification()
    val cameraMode: CameraMode,           // ACTIVE, SILENT (DROP already filtered)
)
```

### Notification.when Extraction
```kotlin
val originTimestamp: Long = sbn.notification.`when`.let { ts ->
    val now = System.currentTimeMillis()
    if (ts > 0 && ts > now - 300_000 && ts <= now + 60_000) ts
    else sbn.postTime  // fallback
}
```

### Camera Name Parser — 3 Regex Strategies
```kotlin
// Strategy 1: "Tapo C200 - Front Gate detected a person"
val p1 = Regex("""Tapo \w+ - (.+?) detected""", RegexOption.IGNORE_CASE)

// Strategy 2: "Front Gate: Person detected"
val p2 = Regex("""^(.+?):\s*(Person|Camera|Motion)""", RegexOption.IGNORE_CASE)

// Strategy 3: "detected on Barn Cam"
val p3 = Regex("""detected on (.+)$""", RegexOption.IGNORE_CASE)

// Fallback: "Unknown-{hash6}"
```

### Event Type Parser
```kotlin
fun parseEventType(title: String, text: String): EventType {
    val combined = "$title $text".lowercase()
    return when {
        "tamper" in combined -> EventType.TAMPERING
        "person" in combined -> EventType.PERSON
        else -> EventType.UNKNOWN
    }
}
```

### ANR Prevention
```kotlin
// onNotificationPosted runs on the main thread.
// Keep it under 10ms by dispatching ALL heavy work to coroutine:
override fun onNotificationPosted(sbn: StatusBarNotification) {
    if (sbn.packageName != TAPO_PACKAGE) return
    
    val key = sbn.key
    val ts = extractTimestamp(sbn)
    val title = sbn.notification.extras.getString(Notification.EXTRA_TITLE) ?: ""
    val text = sbn.notification.extras.getString(Notification.EXTRA_TEXT) ?: ""
    
    // Dispatch to background IMMEDIATELY
    serviceScope.launch(Dispatchers.Default) {
        processNotification(key, ts, title, text)
    }
}
```

## Test Requirements

### TapoNotificationParserTest (Unit)
- "Tapo C200 - Front Gate detected a person" → cameraName = "Front Gate"
- "Barn Cam: Person detected" → cameraName = "Barn Cam"
- "Camera Tampering detected on West Fence" → cameraName = "West Fence"
- Unrecognizable format → "Unknown-{hash}" (never null or empty)
- "Person Detected" title → EventType.PERSON
- "Camera Tampering Detected" title → EventType.TAMPERING
- "Motion Detected" → EventType.UNKNOWN
- Empty strings → no crash, returns UNKNOWN + fallback name

### TapoNotificationListenerTest (Unit with mocks)
- Notification from "com.tplink.iot" → processed
- Notification from "com.whatsapp" → ignored (verify no DAO calls)
- Notification.when = valid recent timestamp → used as originTimestamp
- Notification.when = 0 → falls back to postTime
- Notification.when = epoch 1970 → falls back to postTime
- Camera mode DROP → cancelNotification called, no ThreatRouter dispatch
- Camera mode SILENT → cancelNotification called, ThreatRouter dispatched
- Camera mode ACTIVE → cancelNotification NOT called yet, ThreatRouter dispatched

## Definition of Done
- Service in AndroidManifest with BIND_NOTIFICATION_LISTENER_SERVICE permission
- Parser handles all known Tapo notification formats
- Timestamp extraction always produces valid millis (never 0)
- All tests pass
