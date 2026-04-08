# Kṣetra Sevakaḥ (क्षेत्र सेवकः) — Field Servant AI Platform

> An Android-native, on-device AI platform for agricultural motor management. Zero cloud dependency for AI inference.

## Target Device
- **Xiaomi 14 Civi** — Snapdragon 8s Gen 3, 12 GB RAM, powerful GPU/NPU
- **Framework:** Android Jetpack Compose, Material Design 3, Kotlin
- **AI Engine:** MLC-LLM (local on-device inference — no cloud APIs)

## Primary Module: PumpIQ
Intelligent manager for a **Taro Smart Panel** that communicates via SMS telemetry.

### Core Capabilities
1. **Two-Way SMS Control** — Start/Stop/Status commands to `070936 52065`
2. **HybridAI Architecture** — 0.5B ingestion model + 3B orchestrator model
3. **AI Prediction Engine** — Power failure, fault, worker behavior predictions
4. **Structured Visualizations** — Phase currents, grid reliability, fault distribution
5. **Persistent Storage** — SQLite + Local Vector Store (FAISS) + Google Drive backup

## Module 2: Surakṣā (Security Camera Intelligence)
AI-powered security for 11→30+ TP-Link Tapo cameras.

### Core Capabilities
1. **NotificationListenerService** — Intercepts Tapo app (`com.tplink.iot`) notifications
2. **Notification.when Extraction** — Uses camera's origin timestamp for exact NVR video scrubbing
3. **0.5B Threat Router** — Classifies LOW/MEDIUM/HIGH/CRITICAL, dismisses noise, escalates threats
4. **Camera Configuration Matrix** — Per-camera ACTIVE/SILENT/DROP modes, auto-discovery
5. **Critical Alarm System** — DND override, loud alarm, full-screen red overlay for tampering
6. **Cross-Module Forensic AI** — 3B model correlates security events with PumpIQ telemetry
7. **Security Briefings** — AI-generated nightly summaries with cross-module intelligence

## Repository Structure
```
├── docs/
│   ├── stories/           # Cursor-implementable user stories (ordered)
│   ├── architecture/      # Technical architecture documents
│   ├── test-plan/         # Test strategy & coverage plan
│   └── assets/            # Design tokens, color specs, mock data
├── prototype/             # Interactive UI prototype (React JSX)
├── .cursor/               # Cursor AI configuration
│   └── rules              # Cursor rules file
└── README.md
```

## Implementation Order
Stories are numbered and must be implemented **in sequence**. Each story has:
- Clear acceptance criteria (testable assertions)
- Dependencies on prior stories
- Cursor-executable instructions
- File paths to create/modify

### Epic Flow
```
E01: Project Scaffold & DI
E02: Data Layer (SQLite + Room)
E03: SMS Engine (Send + Receive)
E04: AI Ingestion (0.5B Model)
E05: UI Shell & Navigation
E06: Dashboard & Motor Control
E07: Prediction Engine
E08: AI Chat (3B Orchestrator)
E09: Vector Store & RAG
E10: Google Drive Backup
E11: Integration & Polish
```

## Getting Started (for Cursor)
1. Open this repo in Cursor
2. Read `.cursor/rules` for project conventions
3. Start with `docs/stories/E01-S01-project-scaffold.md`
4. Implement each story sequentially, running tests after each

## Design Reference
See `prototype/ksetra-sevakah.jsx` for the full interactive UI prototype.
See `docs/assets/design-tokens.json` for colors, typography, spacing.
