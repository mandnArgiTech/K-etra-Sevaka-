# E01-S01: Project Scaffold, Gradle Config & Hilt Setup

**Epic:** 01 — Project Scaffold & DI  
**Size:** L (4-8h)  
**Dependencies:** None (first story)

## Description
Set up the Android project with multi-module Gradle structure, Hilt dependency injection, and all required library dependencies. This is the foundation every other story builds on.

## Acceptance Criteria
- [ ] AC1: Project compiles with `./gradlew assembleDebug` — zero errors
- [ ] AC2: Hilt `@HiltAndroidApp` Application class exists and app launches to blank screen
- [ ] AC3: All dependency versions are defined in `libs.versions.toml` catalog
- [ ] AC4: `minSdk = 28`, `targetSdk = 35`, `compileSdk = 35`
- [ ] AC5: Kotlin 2.0+, Compose BOM, Room, Hilt, WorkManager, Navigation Compose all resolve
- [ ] AC6: A `@HiltViewModel` with `@Inject constructor` compiles and injects successfully
- [ ] AC7: JUnit5 test runs with `./gradlew test` — at least one passing test
- [ ] AC8: Compose UI test infrastructure works — at least one passing `@Test` with `composeTestRule`

## Files to Create

### Root-level Gradle
```
build.gradle.kts                    # Root build file
settings.gradle.kts                 # Include modules
gradle/libs.versions.toml           # Version catalog
```

### App Module
```
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/java/com/ksetrasevakah/app/KsetraSevakahApp.kt        # @HiltAndroidApp
app/src/main/java/com/ksetrasevakah/app/MainActivity.kt             # @AndroidEntryPoint
app/src/main/java/com/ksetrasevakah/core/common/Result.kt           # Result<T> wrapper
app/src/main/java/com/ksetrasevakah/core/common/Constants.kt        # App-wide constants
app/src/main/java/com/ksetrasevakah/core/di/AppModule.kt            # Hilt @Module
```

### Test Files
```
app/src/test/java/com/ksetrasevakah/core/common/ResultTest.kt
app/src/androidTest/java/com/ksetrasevakah/app/MainActivityTest.kt
```

## Implementation Details

### libs.versions.toml — Key Dependencies
```toml
[versions]
kotlin = "2.0.21"
compose-bom = "2024.12.01"
room = "2.6.1"
hilt = "2.53.1"
navigation = "2.8.5"
workmanager = "2.10.0"
junit5 = "5.11.4"
mockk = "1.13.13"
turbine = "1.2.0"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-navigation = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }
workmanager = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "workmanager" }
junit5-api = { group = "org.junit.jupiter", name = "junit-jupiter-api", version.ref = "junit5" }
junit5-engine = { group = "org.junit.jupiter", name = "junit-jupiter-engine", version.ref = "junit5" }
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
```

### Constants.kt
```kotlin
package com.ksetrasevakah.core.common

object Constants {
    const val TARO_PANEL_NUMBER = "070936 52065"
    const val INGESTION_MODEL_ID = "Qwen2.5-0.5B-Instruct-q4f16_1-MLC"
    const val ORCHESTRATOR_MODEL_ID = "Qwen2.5-3B-Instruct-q4f16_1-MLC"
    const val DB_NAME = "ksetra_sevakah_db"
    const val VECTOR_DB_DIR = "vector_store"
    const val BACKUP_FOLDER_NAME = "KsetraSevakah_Backup"
    const val SMS_TIMEOUT_SECONDS = 30L
    const val PREDICTION_WINDOW_DAYS = 14
    const val FORGOT_OFF_THRESHOLD_MINUTES = 45
    const val MAX_CHAT_CONTEXT_MESSAGES = 10
    const val VECTOR_SEARCH_TOP_K = 5
    const val BACKUP_SCHEDULE_HOUR = 2
}
```

### Result.kt
```kotlin
package com.ksetrasevakah.core.common

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isLoading get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data
    fun getOrThrow(): T = (this as Success).data
}
```

## Test Requirements

### Unit Test: ResultTest.kt
```kotlin
// Test: Success wraps data correctly
// Test: Error wraps message and throwable
// Test: getOrNull returns null for Error
// Test: getOrThrow throws for Error
// Test: isSuccess/isError/isLoading flags
```

### Instrumented Test: MainActivityTest.kt
```kotlin
// Test: Activity launches without crash
// Test: Compose content is set (composeTestRule.onRoot().assertExists())
```

## Definition of Done
- `./gradlew assembleDebug` succeeds
- `./gradlew test` passes with ≥1 unit test
- `./gradlew connectedAndroidTest` passes with ≥1 instrumented test
- Hilt graph compiles without errors
