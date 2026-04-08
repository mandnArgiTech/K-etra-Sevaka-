# FIX Stories — Code Review Remediation

> These stories fix all issues found during the code review.
> Implement sequentially. Each fix is small, focused, and independently testable.
> Priority: 🔴 CRITICAL → 🟡 IMPORTANT → 🟢 MINOR

| Story | Severity | Title | Est. |
|-------|----------|-------|------|
| [FIX-01](./FIX-01-remove-build-artifacts.md) | 🔴 CRITICAL | Remove `.gradle/` and `.kotlin/` from git tracking | S |
| [FIX-02](./FIX-02-manifest-declarations.md) | 🔴 CRITICAL | Register NotificationListenerService + SmsReceiver in AndroidManifest | S |
| [FIX-03](./FIX-03-notification-dismissal.md) | 🔴 CRITICAL | Fix notification cancellation (anti-fatigue) — pass sbnKey through pipeline | M |
| [FIX-04](./FIX-04-threat-time-of-day-rules.md) | 🔴 CRITICAL | Add time-of-day rule classification for PERSON events in ThreatClassifier | M |
| [FIX-05](./FIX-05-unacknowledged-flow.md) | 🔴 CRITICAL | Change `getUnacknowledgedHighCount()` from `suspend` to `Flow<Int>` | S |
| [FIX-06](./FIX-06-notification-when-fallback.md) | 🟡 IMPORTANT | Fix `Notification.when` validation to fallback instead of discard | S |
| [FIX-07](./FIX-07-cross-module-correlator.md) | 🟡 IMPORTANT | Wire CrossModuleCorrelator to PumpIQ repositories | M |
| [FIX-08](./FIX-08-system-prompt-suraksha.md) | 🟡 IMPORTANT | Add Surakṣā context to SystemPromptBuilder | M |
| [FIX-09](./FIX-09-dao-instrumented-tests.md) | 🟡 IMPORTANT | Add instrumented DAO tests for all 11 DAOs | L |
| [FIX-10](./FIX-10-acknowledged-int-type.md) | 🟡 IMPORTANT | Change SecurityEventEntity.acknowledged from Boolean to Int | S |
| [FIX-11](./FIX-11-camera-config-id-type.md) | 🟢 MINOR | Fix CameraConfig domain model id from String to Long | S |
| [FIX-12](./FIX-12-classifier-model-id.md) | 🟢 MINOR | Use 0.5B INGESTION model for threat classification, not 3B | S |
| [FIX-13](./FIX-13-motor-state-error-feedback.md) | 🟢 MINOR | Return Result.Error for invalid motor state transitions | S |
| [FIX-14](./FIX-14-room-migration-strategy.md) | 🟢 MINOR | Replace fallbackToDestructiveMigration with proper Migration | M |
| [FIX-15](./FIX-15-proguard-rules.md) | 🟢 MINOR | Add missing proguard-rules.pro for release builds | S |

**Total: 15 fixes — ~4 CRITICAL, 5 IMPORTANT, 6 MINOR**
**Estimated: ~20-25 hours**
