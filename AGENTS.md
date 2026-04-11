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

- No Android emulator — instrumented tests (`connectedAndroidTest`) cannot run in Cloud. Unit tests are comprehensive.
- On-device LLM uses **LiteRT-LM** (`litertlm-android 0.9.0`, `.litertlm` format under `filesDir/models/`). `Backend.GPU()` routes through OpenCL on Adreno (Snapdragon 8s Gen 3). First-run download is large; Cloud builds do not run inference. SMS ingestion uses a fast regex parser (`RegexSmsParser`) — no ingestion model is downloaded.
- **Google Drive backup** needs a GCP project, Drive API enabled, and an Android OAuth client ID (package + SHA-1) for sign-in and uploads to work on a real device.
- ESLint on prototype has 1 pre-existing warning (`react-hooks/exhaustive-deps`).
- No Docker or server-side secrets required for **building** and **unit tests**; model URLs hit the public internet only when downloading assets on device.
