# E15-S01: Camera Configuration Matrix Screen

**Epic:** 15 — Camera Management  
**Size:** M (2-4h)  
**Dependencies:** E12-S01, E05-S01

## Description
Settings screen listing all auto-discovered cameras with their mode (ACTIVE/SILENT/DROP), event counts, last-seen time, and the ability to change modes. Handles 30+ cameras with search/filter.

## Acceptance Criteria
- [ ] AC1: Screen title "Camera Matrix" with shield icon
- [ ] AC2: Each camera row shows: icon, camera name (editable display name), mode selector, event count, last seen time
- [ ] AC3: Mode selector: segmented control with 3 options — ACTIVE (blue), SILENT (gray), DROP (dark)
- [ ] AC4: Changing mode → `CameraConfigDao.updateMode()` → immediate effect on notification processing
- [ ] AC5: Camera list sorted by lastSeen DESC (most recently active first)
- [ ] AC6: Search bar to filter cameras by name
- [ ] AC7: Each row shows location tag (editable) — e.g., "Perimeter", "Main Gate", "Barn"
- [ ] AC8: Swipe to reveal "Edit Name" action
- [ ] AC9: Header shows total camera count + active/silent/drop breakdown
- [ ] AC10: "Set All Active" / "Set All Silent" bulk action buttons
- [ ] AC11: Empty state: "No cameras discovered yet. They'll appear when their first notification arrives."
- [ ] AC12: Supports 30+ cameras with lazy list performance

## Files to Create
```
app/src/main/java/com/ksetrasevakah/feature/suraksha/camera/
├── CameraMatrixScreen.kt
├── CameraMatrixViewModel.kt
├── model/
│   ├── CameraMatrixUiState.kt
│   └── CameraMatrixUiEvent.kt
└── component/
    ├── CameraConfigRow.kt
    └── CameraModeSelector.kt

app/src/test/java/com/ksetrasevakah/feature/suraksha/camera/
└── CameraMatrixViewModelTest.kt

app/src/androidTest/java/com/ksetrasevakah/feature/suraksha/camera/
└── CameraMatrixScreenTest.kt
```

## Implementation Details

### CameraConfigRow Visual Spec
```
┌─────────────────────────────────────────────────────────────────┐
│  📷 Front Gate                                    47 events     │
│      Perimeter                          last seen: 2 min ago    │
│                                                                 │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                       │
│  │ ACTIVE ✓ │ │  SILENT  │ │   DROP   │                       │
│  │  (blue)  │ │  (gray)  │ │  (dark)  │                       │
│  └──────────┘ └──────────┘ └──────────┘                       │
└─────────────────────────────────────────────────────────────────┘
```

### CameraModeSelector — Segmented Control
```kotlin
// 3-segment toggle: ACTIVE | SILENT | DROP
// Selected segment: filled with mode color
// Unselected: outlined
// Mode change → immediate DAO update
// Confirm dialog for DROP: "This camera's notifications will be deleted. Continue?"
```

## Test Requirements

### CameraMatrixViewModelTest
- Load cameras → UiState populated from Flow
- Change mode → DAO.updateMode called with correct params
- Search filter → list filtered by name
- Bulk "Set All Silent" → all cameras updated

### CameraMatrixScreenTest
- Camera rows rendered with correct names and counts
- Mode selector shows current mode highlighted
- Tap SILENT on a row → mode changes
- Search filters camera list
- Empty state when no cameras
