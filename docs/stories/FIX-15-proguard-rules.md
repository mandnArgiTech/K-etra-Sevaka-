# FIX-15: Add Missing proguard-rules.pro for Release Builds

**Severity:** 🟢 MINOR  
**Size:** S (< 30 min)  
**Dependencies:** None

## Problem
`build.gradle.kts` references `proguard-rules.pro` in the release build type, but the file doesn't exist. Release builds will fail with a file-not-found error.

## Acceptance Criteria
- [ ] AC1: `app/proguard-rules.pro` file exists
- [ ] AC2: Contains rules to keep Room entities, Hilt components, and Kotlin serialization
- [ ] AC3: `./gradlew assembleRelease` succeeds (may need signing config — at minimum no proguard error)

## Fix — Create app/proguard-rules.pro
```proguard
# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# MLC-LLM (if using JNI)
-keep class ai.mlc.mlcllm.** { *; }

# App entities
-keep class com.ksetrasevakah.core.database.entity.** { *; }
-keep class com.ksetrasevakah.core.database.model.** { *; }
```

## Files to Create
```
app/proguard-rules.pro
```
