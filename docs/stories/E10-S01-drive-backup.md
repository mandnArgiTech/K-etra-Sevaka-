# E10-S01: Google Drive Auto-Backup (WorkManager)

**Epic:** 10 — Google Drive Backup  
**Size:** L (4-8h)  
**Dependencies:** E02-S01

## Description
Automated daily backup of Room database, FAISS index, and chat data to Google Drive using WorkManager scheduled at 02:00 AM.

## Acceptance Criteria
- [ ] AC1: `BackupWorker` extends `CoroutineWorker` and runs via WorkManager
- [ ] AC2: Scheduled daily at 02:00 AM with `PeriodicWorkRequest`
- [ ] AC3: Exports Room DB to zip file
- [ ] AC4: Exports FAISS index + metadata to zip
- [ ] AC5: Uploads zip to Google Drive folder "KsetraSevakah_Backup"
- [ ] AC6: Creates Drive folder if not exists
- [ ] AC7: Keeps last 7 backups, deletes older ones
- [ ] AC8: Manual backup trigger from Settings screen
- [ ] AC9: `BackupLogEntity` records each backup (timestamp, size, status)
- [ ] AC10: OAuth2 sign-in flow for Google Drive access
- [ ] AC11: Backup status exposed as `Flow` for UI observation
- [ ] AC12: Network unavailable → retry with exponential backoff

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/backup/
├── BackupWorker.kt
├── DriveApiClient.kt
├── BackupManager.kt
├── model/BackupStatus.kt
└── di/BackupModule.kt

app/src/test/java/com/ksetrasevakah/core/backup/BackupManagerTest.kt
app/src/test/java/com/ksetrasevakah/core/backup/BackupWorkerTest.kt
```

## Test Requirements
- BackupManager creates zip with DB + FAISS files
- Upload to Drive called with correct folder name
- Old backups (> 7) deleted
- Network failure → Worker returns Result.retry()
- BackupLog entry created on success/failure
