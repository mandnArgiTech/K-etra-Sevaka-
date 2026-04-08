# E10-S02: Import/Restore from Google Drive

**Epic:** 10 — Google Drive Backup  
**Size:** M (2-4h)  
**Dependencies:** E10-S01

## Description
Restore app data from Google Drive backup: download zip, extract, replace local Room DB, rebuild FAISS index.

## Acceptance Criteria
- [ ] AC1: `RestoreManager` lists available backups from Drive folder
- [ ] AC2: User selects backup → downloads zip
- [ ] AC3: Room DB replaced with backup copy (app restarts DB connection)
- [ ] AC4: FAISS index restored from backup
- [ ] AC5: Confirmation dialog before restore ("This will replace all current data")
- [ ] AC6: Progress indicator during download + restore
- [ ] AC7: Restore failure → rollback to previous state, show error
- [ ] AC8: BackupLog entry records restore event

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/backup/
└── RestoreManager.kt

app/src/test/java/com/ksetrasevakah/core/backup/RestoreManagerTest.kt
```

## Test Requirements
- List backups → returns sorted by date DESC
- Download + extract → files in correct locations
- DB replacement → DAOs still work after restore
- Corrupted zip → error handled, no data loss
