# E01-S02: Design System — Theme, Colors, Typography, Reusable Components

**Epic:** 01 — Project Scaffold & DI  
**Size:** M (2-4h)  
**Dependencies:** E01-S01

## Description
Implement the Kṣetra Sevakaḥ design system in Jetpack Compose: dark agricultural theme with earthy greens, high-contrast typography for sunlight readability, and reusable composable components.

## Acceptance Criteria
- [ ] AC1: `KsetraTheme` composable wraps Material3 `MaterialTheme` with custom `ColorScheme`
- [ ] AC2: Colors from `docs/assets/design-tokens.json` are mapped to `Color` objects in `KsetraColors.kt`
- [ ] AC3: Custom typography scale uses Playfair Display (display), DM Sans (body), JetBrains Mono (data)
- [ ] AC4: `RiskBadge(level: RiskLevel)` composable renders Low/Medium/High/Critical with correct colors
- [ ] AC5: `GlowEffect` composable renders radial gradient background decoration
- [ ] AC6: `KsetraCard` composable provides standard card styling (background, border, radius)
- [ ] AC7: `MotorStateIndicator(state: MotorState)` renders dot + label with correct color per state
- [ ] AC8: Preview annotations work for all components — visible in Compose Preview pane
- [ ] AC9: All colors pass WCAG AA contrast ratio against `background` for text readability

## Files to Create
```
app/src/main/java/com/ksetrasevakah/designsystem/
├── theme/
│   ├── KsetraTheme.kt              # MaterialTheme wrapper
│   ├── KsetraColors.kt             # Color definitions + RiskLevel colors
│   ├── KsetraTypography.kt         # Type scale
│   └── KsetraSpacing.kt            # Spacing tokens
├── component/
│   ├── RiskBadge.kt                # Risk level badge composable
│   ├── GlowEffect.kt              # Radial glow background
│   ├── KsetraCard.kt               # Standard card wrapper
│   ├── MotorStateIndicator.kt      # Dot + label for motor state
│   ├── SectionHeader.kt            # Uppercase label with icon
│   └── PhaseCurrentMini.kt         # Mini phase current display (R/Y/B)
└── model/
    ├── MotorState.kt               # enum: OFF, PENDING_START, ON, PENDING_STOP
    └── RiskLevel.kt                # enum: LOW, MEDIUM, HIGH, CRITICAL
```

### Test Files
```
app/src/test/java/com/ksetrasevakah/designsystem/model/MotorStateTest.kt
app/src/test/java/com/ksetrasevakah/designsystem/model/RiskLevelTest.kt
app/src/androidTest/java/com/ksetrasevakah/designsystem/component/RiskBadgeTest.kt
app/src/androidTest/java/com/ksetrasevakah/designsystem/component/MotorStateIndicatorTest.kt
```

## Implementation Details

### MotorState.kt
```kotlin
enum class MotorState {
    OFF, PENDING_START, ON, PENDING_STOP;

    val isOff get() = this == OFF
    val isOn get() = this == ON
    val isPending get() = this == PENDING_START || this == PENDING_STOP
    val displayLabel: String get() = when (this) {
        OFF -> "Offline"
        PENDING_START -> "Starting..."
        ON -> "Running"
        PENDING_STOP -> "Stopping..."
    }
}
```

### RiskLevel.kt
```kotlin
enum class RiskLevel {
    LOW, MEDIUM, HIGH, CRITICAL;

    companion object {
        fun fromPercentage(pct: Int): RiskLevel = when {
            pct < 25 -> LOW
            pct < 50 -> MEDIUM
            pct < 75 -> HIGH
            else -> CRITICAL
        }
    }
}
```

### KsetraColors.kt — Key Colors
```kotlin
val KsetraDarkBackground = Color(0xFF0F1A12)
val KsetraSurface = Color(0xFF142219)
val KsetraCard = Color(0xFF1A2B1E)
val KsetraBorder = Color(0xFF2D4A35)
val KsetraAccentGreen = Color(0xFF4ADE80)
val KsetraAccentGreenDim = Color(0xFF22623D)
val KsetraAmber = Color(0xFFF59E0B)
val KsetraRed = Color(0xFFEF4444)
val KsetraPurple = Color(0xFFA78BFA)
val KsetraBlue = Color(0xFF60A5FA)
val KsetraTextPrimary = Color(0xFFF0FFF4)
val KsetraTextSecondary = Color(0xFFA3C4AE)
```

## Test Requirements

### Unit: MotorStateTest
- `OFF.isOff` returns true, `isOn` false, `isPending` false
- `PENDING_START.isPending` returns true
- `displayLabel` returns correct string for each state

### Unit: RiskLevelTest
- `fromPercentage(10)` → LOW
- `fromPercentage(30)` → MEDIUM
- `fromPercentage(60)` → HIGH
- `fromPercentage(80)` → CRITICAL
- Boundary: `fromPercentage(25)` → MEDIUM, `fromPercentage(24)` → LOW

### Compose UI: RiskBadgeTest
- Badge renders text "LOW" for `RiskLevel.LOW`
- Badge renders with green tint for LOW, amber for MEDIUM, red for HIGH
- Badge is visible and has correct semantics

### Compose UI: MotorStateIndicatorTest
- Renders "Running" text for `MotorState.ON`
- Renders "Offline" text for `MotorState.OFF`

## Definition of Done
- All composables render in Preview without crash
- All tests pass
- Theme applied to `MainActivity` content
