# FIX-06: Fix Notification.when Validation — Fallback Instead of Discard

**Severity:** 🟡 IMPORTANT  
**Size:** S (< 30 min)  
**Dependencies:** None

## Problem
The listener silently **discards** notifications where `Notification.when` is >5 min old:
```kotlin
if (whenMs > 0 && System.currentTimeMillis() - whenMs > Constants.NOTIFICATION_WHEN_MAX_AGE_MS) {
    return  // ← DROPS the notification entirely!
}
```
If a phone was in Doze mode or had network delay, a legitimate alert from 6 minutes ago would be permanently lost. The architecture spec says to **fallback to `sbn.postTime`**, not discard.

## Acceptance Criteria
- [ ] AC1: If `Notification.when` is valid and within 5 min → use it (unchanged)
- [ ] AC2: If `Notification.when` is 0 → fallback to `System.currentTimeMillis()`
- [ ] AC3: If `Notification.when` is >5 min old → fallback to `System.currentTimeMillis()`, do NOT discard
- [ ] AC4: If `Notification.when` is in the future (>60s ahead) → fallback
- [ ] AC5: Event is ALWAYS processed regardless of timestamp age
- [ ] AC6: Unit test covers all 4 timestamp scenarios

## Fix
```kotlin
// TapoNotificationListener.kt — replace the early return with fallback:
val now = System.currentTimeMillis()
val originTimestamp = when {
    whenMs <= 0 -> now
    whenMs > now + 60_000 -> now                           // future = clock skew
    now - whenMs > Constants.NOTIFICATION_WHEN_MAX_AGE_MS -> now  // old = fallback
    else -> whenMs                                          // valid = use it
}
// REMOVE the `return` — always continue processing with the resolved timestamp
```

## Files to Modify
```
app/src/main/java/com/ksetrasevakah/core/notification/TapoNotificationListener.kt
app/src/test/java/com/ksetrasevakah/core/notification/TapoNotificationParserTest.kt
```

## Test Requirements
- whenMs = valid recent → originTimestamp = whenMs
- whenMs = 0 → originTimestamp ≈ now
- whenMs = 10 min ago → originTimestamp ≈ now (fallback, NOT discarded)
- whenMs = 2 min in future → originTimestamp ≈ now (fallback)
