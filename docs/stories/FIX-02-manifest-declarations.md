# FIX-02: Register NotificationListenerService + SmsReceiver in AndroidManifest

**Severity:** 🔴 CRITICAL  
**Size:** S (< 1h)  
**Dependencies:** None

## Problem
`TapoNotificationListener` and `SmsReceiver` are implemented but **never registered** in `AndroidManifest.xml`. Without manifest declarations, Android will never instantiate these components — the entire SMS engine and Tapo notification interception are dead code at runtime.

## Current State
```xml
<!-- app/src/main/AndroidManifest.xml — only has MainActivity -->
<application ...>
    <activity android:name=".app.MainActivity" ... />
    <!-- MISSING: TapoNotificationListener service -->
    <!-- MISSING: SmsReceiver broadcast receiver -->
    <!-- MISSING: SmsSentReceiver broadcast receiver -->
    <!-- MISSING: IngestionService foreground service -->
</application>
```

## Acceptance Criteria
- [ ] AC1: `TapoNotificationListener` registered as `<service>` with `BIND_NOTIFICATION_LISTENER_SERVICE` permission and correct intent-filter
- [ ] AC2: `SmsReceiver` registered as `<receiver>` with `SMS_RECEIVED` intent-filter and priority 999
- [ ] AC3: `SmsSentReceiver` registered as `<receiver>` with the custom `SMS_SENT` action
- [ ] AC4: `IngestionService` registered as `<service>` with `FOREGROUND_SERVICE` type
- [ ] AC5: `FOREGROUND_SERVICE_DATA_SYNC` permission added (required on API 34+)
- [ ] AC6: App compiles and launches without manifest merge errors
- [ ] AC7: Notification listener appears in device Settings → Notifications → Notification access

## Fix — Add to AndroidManifest.xml

Inside `<application>` tag, add:

```xml
<!-- Surakṣā: Tapo Notification Interception -->
<service
    android:name=".core.notification.TapoNotificationListener"
    android:label="Kṣetra Sevakaḥ Security Monitor"
    android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
    android:exported="false">
    <intent-filter>
        <action android:name="android.service.notification.NotificationListenerService" />
    </intent-filter>
</service>

<!-- PumpIQ: SMS Receive from Taro Panel -->
<receiver
    android:name=".core.sms.SmsReceiver"
    android:exported="true"
    android:permission="android.permission.BROADCAST_SMS">
    <intent-filter android:priority="999">
        <action android:name="android.provider.Telephony.SMS_RECEIVED" />
    </intent-filter>
</receiver>

<!-- PumpIQ: SMS Send Confirmation -->
<receiver
    android:name=".core.sms.SmsSentReceiver"
    android:exported="false">
    <intent-filter>
        <action android:name="com.ksetrasevakah.SMS_SENT" />
    </intent-filter>
</receiver>

<!-- AI Ingestion Foreground Service -->
<service
    android:name=".core.ai.IngestionService"
    android:foregroundServiceType="dataSync"
    android:exported="false" />
```

Also add permission at the top level `<manifest>`:
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
```

## Files to Modify
```
app/src/main/AndroidManifest.xml
```

## Test Requirements
- App compiles: `./gradlew assembleDebug` succeeds
- No manifest merge errors in build log
- Verify with: `aapt dump xmltree app/build/outputs/apk/debug/app-debug.apk AndroidManifest.xml | grep -E "service|receiver"` — should list all 4 components
