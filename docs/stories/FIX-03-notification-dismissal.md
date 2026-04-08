# FIX-03: Fix Notification Cancellation — Pass sbnKey Through Pipeline

**Severity:** 🔴 CRITICAL  
**Size:** M (2-4h)  
**Dependencies:** FIX-02

## Problem
The core anti-fatigue feature is broken. When a LOW/MEDIUM threat is detected, the Tapo notification should be **actively cancelled** so the farmer doesn't get bombarded with 200+ daytime alerts. Currently:

1. `TapoEvent` has no `sbnKey` field — the notification key is never captured
2. `ThreatRouter` has no reference to `NotificationListenerService` to call `cancelNotification()`
3. For SILENT mode cameras, notifications are never dismissed either

The farmer sees every single Tapo notification — defeating the entire purpose of the AI router.

## Current Code (Broken)
```kotlin
// TapoEvent — no sbnKey
data class TapoEvent(
    val cameraName: String,
    val eventType: String,
    val timestamp: Long,
    val rawTitle: String,
    val rawText: String
    // MISSING: sbnKey for notification cancellation
)

// TapoNotificationListener — never stores sbn.key
override fun onNotificationPosted(sbn: StatusBarNotification?) {
    // ... parses but never captures sbn.key
    val event: TapoEvent = TapoNotificationParser.parse(title, text, whenMs)
    // event has no way to reference the original notification
}

// ThreatRouter — no way to dismiss notifications
// Has no reference to the NotificationListenerService
```

## Acceptance Criteria
- [ ] AC1: `TapoEvent` has `sbnKey: String` field
- [ ] AC2: `TapoNotificationListener.onNotificationPosted` captures `sbn.key` into `TapoEvent.sbnKey`
- [ ] AC3: `ThreatRouter` receives a `NotificationDismisser` interface to cancel notifications
- [ ] AC4: LOW threat + ACTIVE mode → `cancelNotification(sbnKey)` called
- [ ] AC5: MEDIUM threat + ACTIVE mode → `cancelNotification(sbnKey)` called
- [ ] AC6: HIGH threat + ACTIVE mode → notification stays (NO cancel)
- [ ] AC7: CRITICAL threat + ACTIVE mode → notification stays (NO cancel)
- [ ] AC8: Any threat + SILENT mode → `cancelNotification(sbnKey)` called (always dismiss)
- [ ] AC9: DROP mode → `cancelNotification(sbnKey)` called immediately in listener (before router)
- [ ] AC10: Unit tests verify cancelNotification called/not-called for each scenario

## Fix Steps

### Step 1: Add sbnKey to TapoEvent
```kotlin
// core/notification/model/TapoEvent.kt
data class TapoEvent(
    val cameraName: String,
    val eventType: String,
    val timestamp: Long,
    val rawTitle: String,
    val rawText: String,
    val sbnKey: String       // ← ADD: notification key for cancellation
)
```

### Step 2: Create NotificationDismisser interface
```kotlin
// core/notification/NotificationDismisser.kt
interface NotificationDismisser {
    fun dismiss(sbnKey: String)
}
```

### Step 3: Implement in TapoNotificationListener
```kotlin
class TapoNotificationListener : NotificationListenerService(), NotificationDismisser {
    
    override fun dismiss(sbnKey: String) {
        cancelNotification(sbnKey)
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        if (sbn.packageName != Constants.TAPO_PACKAGE_NAME) return
        
        // ... extract title, text, whenMs ...
        
        val event = TapoNotificationParser.parse(title, text, whenMs, sbn.key) // pass key
        
        scope.launch {
            threatRouter.route(event, this@TapoNotificationListener) // pass dismisser
        }
    }
}
```

### Step 4: Update ThreatRouter to dismiss
```kotlin
class ThreatRouter @Inject constructor(...) {

    suspend fun route(event: TapoEvent, dismisser: NotificationDismisser) {
        val mode = /* get camera mode */
        
        // DROP mode: dismiss immediately, no processing
        if (!mode.shouldProcess) {
            dismisser.dismiss(event.sbnKey)
            return
        }
        
        // ... classify, persist ...
        
        // Dismiss based on mode + threat level
        if (!mode.shouldAlert) {
            // SILENT mode: always dismiss
            dismisser.dismiss(event.sbnKey)
        } else {
            // ACTIVE mode: dismiss LOW/MEDIUM, keep HIGH/CRITICAL
            val threatLevel = /* from classification */
            if (!threatLevel.isAlertable) {
                dismisser.dismiss(event.sbnKey)
            }
        }
        
        // Execute remaining action (alarm, notify, etc.)
        executeAction(action)
    }
}
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/notification/model/TapoEvent.kt
app/src/main/java/com/ksetrasevakah/core/notification/TapoNotificationListener.kt
app/src/main/java/com/ksetrasevakah/core/notification/TapoNotificationParser.kt
app/src/main/java/com/ksetrasevakah/feature/suraksha/prediction/ThreatRouter.kt
```

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/notification/NotificationDismisser.kt
```

## Test Requirements

### ThreatRouterTest (update existing)
- LOW + ACTIVE → `dismisser.dismiss()` called with correct sbnKey
- MEDIUM + ACTIVE → `dismisser.dismiss()` called
- HIGH + ACTIVE → `dismisser.dismiss()` NOT called
- CRITICAL + ACTIVE → `dismisser.dismiss()` NOT called
- Any threat + SILENT → `dismisser.dismiss()` called
- DROP mode → `dismisser.dismiss()` called, no classify/persist

### TapoNotificationParserTest (update)
- Verify sbnKey is passed through to TapoEvent

## Definition of Done
- Farmer sees ZERO Tapo notifications for LOW/MEDIUM/SILENT events
- Farmer sees notifications ONLY for HIGH/CRITICAL on ACTIVE cameras
- All tests pass
