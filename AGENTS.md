# AGENTS.md

## Cursor Cloud specific instructions

### Repository overview

Android-native, on-device AI platform for agricultural motor management and security camera intelligence. Two modules: **PumpIQ** (SMS-based motor control + AI predictions) and **Suraksha** (Tapo camera notification interception + threat classification). Architecture docs in `docs/`, user stories in `docs/stories/`, test plans in `docs/test-plan/`.

### Two runnable artifacts

**1. React prototype** (`prototype/`) — interactive UI mockup:

| Action | Command | Working directory |
|--------|---------|-------------------|
| Install deps | `npm install` | `prototype/` |
| Dev server | `npx vite --host 0.0.0.0 --port 5173` | `prototype/` |
| Lint | `npx eslint .` | `prototype/` |
| Build | `npx vite build` | `prototype/` |

**2. Android app** (`app/`) — Kotlin + Jetpack Compose, built with Gradle:

| Action | Command | Working directory |
|--------|---------|-------------------|
| Build debug APK | `./gradlew assembleDebug` | `/workspace` |
| Unit tests (217) | `./gradlew testDebugUnitTest` | `/workspace` |
| All tests | `./gradlew test` | `/workspace` |

Requires `ANDROID_HOME=/opt/android-sdk` (SDK 35 + build-tools 35.0.0). JDK 17+ required (JDK 21 available by default).

### Key caveats

- No Android emulator — instrumented tests (`connectedAndroidTest`) cannot run in Cloud. Unit tests are comprehensive (217 tests).
- MLC-LLM engine is stubbed (`DefaultMlcLlmEngine`) since the real SDK requires a physical Snapdragon device.
- Google Drive API client is stubbed (`DriveApiClient` interface) — requires OAuth2 credentials for real usage.
- ESLint on prototype has 1 pre-existing warning (`react-hooks/exhaustive-deps`).
- No external services, Docker, API keys, or secrets required for building and unit testing.
