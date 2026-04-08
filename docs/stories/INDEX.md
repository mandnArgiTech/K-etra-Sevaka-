# Story Index — Implementation Order

> Each story is self-contained with acceptance criteria, files to create, and test requirements.
> Implement **sequentially** — each story depends on prior stories being complete.

## Epic 01: Project Scaffold & Dependency Injection
| Story | Title | Est. |
|-------|-------|------|
| [E01-S01](./E01-S01-project-scaffold.md) | Project scaffold, Gradle config, Hilt setup | L |
| [E01-S02](./E01-S02-design-system.md) | Design system: Theme, Colors, Typography, Components | M |

## Epic 02: Data Layer (Room + SQLite)
| Story | Title | Est. |
|-------|-------|------|
| [E02-S01](./E02-S01-room-database.md) | Room database, entities, DAOs, migrations | L |
| [E02-S02](./E02-S02-repositories.md) | Repository interfaces & implementations | M |

## Epic 03: SMS Engine
| Story | Title | Est. |
|-------|-------|------|
| [E03-S01](./E03-S01-sms-send.md) | SMS send engine (SmsManager, commands, PendingIntent) | M |
| [E03-S02](./E03-S02-sms-receive.md) | SMS receive engine (BroadcastReceiver, filtering) | M |
| [E03-S03](./E03-S03-sms-state-machine.md) | Motor state machine (OFF→PENDING→ON transitions) | M |

## Epic 04: AI Ingestion Layer (0.5B Model)
| Story | Title | Est. |
|-------|-------|------|
| [E04-S01](./E04-S01-mlc-llm-engine.md) | MLC-LLM engine wrapper & model lifecycle | L |
| [E04-S02](./E04-S02-ingestion-service.md) | Ingestion foreground service & SMS parsing | L |
| [E04-S03](./E04-S03-narrative-generation.md) | Narrative text generation & vector store ingestion | M |

## Epic 05: UI Shell & Navigation
| Story | Title | Est. |
|-------|-------|------|
| [E05-S01](./E05-S01-navigation.md) | NavGraph, MainActivity, screen routing | M |
| [E05-S02](./E05-S02-hub-screen.md) | Hub screen (module launcher) | S |

## Epic 06: Dashboard & Motor Control
| Story | Title | Est. |
|-------|-------|------|
| [E06-S01](./E06-S01-dashboard-screen.md) | Dashboard screen layout & ViewModel | L |
| [E06-S02](./E06-S02-motor-control-card.md) | State-aware motor control card (Start/Stop/Pending) | M |
| [E06-S03](./E06-S03-charts.md) | Data visualizations (Phase, Grid, Faults) | M |

## Epic 07: Prediction Engine
| Story | Title | Est. |
|-------|-------|------|
| [E07-S01](./E07-S01-prediction-core.md) | Prediction engine core (power, fault, worker algorithms) | L |
| [E07-S02](./E07-S02-prediction-cards.md) | Dashboard prediction cards UI | M |
| [E07-S03](./E07-S03-worker-pattern.md) | Worker pattern chart & forgot-OFF watchdog | M |
| [E07-S04](./E07-S04-power-forecast-chart.md) | Power forecast chart with AI voltage prediction | M |

## Epic 08: AI Chat (3B Orchestrator)
| Story | Title | Est. |
|-------|-------|------|
| [E08-S01](./E08-S01-chat-data-layer.md) | Chat threads & messages data layer | M |
| [E08-S02](./E08-S02-chat-screen.md) | AI Analyst chat screen UI | L |
| [E08-S03](./E08-S03-orchestrator-integration.md) | 3B model integration with context & RAG | L |
| [E08-S04](./E08-S04-quick-query-chips.md) | One-click prediction query chips | M |

## Epic 09: Vector Store & RAG
| Story | Title | Est. |
|-------|-------|------|
| [E09-S01](./E09-S01-vector-store.md) | FAISS vector store setup & embedding pipeline | L |
| [E09-S02](./E09-S02-rag-pipeline.md) | RAG retrieval pipeline for chat | M |

## Epic 10: Google Drive Backup
| Story | Title | Est. |
|-------|-------|------|
| [E10-S01](./E10-S01-drive-backup.md) | Google Drive auto-backup (WorkManager) | L |
| [E10-S02](./E10-S02-drive-restore.md) | Import/restore from Google Drive | M |

## Epic 11: Integration & Polish
| Story | Title | Est. |
|-------|-------|------|
| [E11-S01](./E11-S01-settings-screen.md) | Settings screen (Panel config, AI config, backup) | M |
| [E11-S02](./E11-S02-notifications.md) | Notifications (forgot-OFF watchdog, fault alerts) | M |
| [E11-S03](./E11-S03-e2e-flow.md) | End-to-end integration test suite | L |

---

**Size guide:** S = 1-2h, M = 2-4h, L = 4-8h
**Total stories:** 27
**Estimated implementation:** ~100-120 hours

---

# MODULE 2: SURAKṢĀ — Security Camera Intelligence

> Implement AFTER PumpIQ (E01-E11) is complete. Surakṣā reuses the shared core layer.

## Epic 12: Surakṣā Data Layer
| Story | Title | Est. |
|-------|-------|------|
| [E12-S01](./E12-S01-suraksha-data-layer.md) | Database entities, DAOs, repositories for security events & cameras | L |

## Epic 13: Notification Engine & Threat Router
| Story | Title | Est. |
|-------|-------|------|
| [E13-S01](./E13-S01-notification-listener.md) | NotificationListenerService for Tapo camera interception | L |
| [E13-S02](./E13-S02-threat-router.md) | 0.5B Threat Router: classification, routing, notification dismissal | L |
| [E13-S03](./E13-S03-critical-alarm.md) | Critical alarm system: DND override, sound, vibration, overlay state | M |

## Epic 14: Surakṣā Dashboard UI
| Story | Title | Est. |
|-------|-------|------|
| [E14-S01](./E14-S01-suraksha-dashboard.md) | Dashboard: Threat Ledger, AI Briefing, Heatmap, Summary Cards | L |
| [E14-S02](./E14-S02-critical-overlay.md) | Critical threat full-screen red alert overlay | M |

## Epic 15: Camera Management
| Story | Title | Est. |
|-------|-------|------|
| [E15-S01](./E15-S01-camera-matrix.md) | Camera Configuration Matrix screen (ACTIVE/SILENT/DROP modes) | M |

## Epic 16: Cross-Module Intelligence
| Story | Title | Est. |
|-------|-------|------|
| [E16-S01](./E16-S01-cross-module-chat.md) | Cross-module AI chat: PumpIQ + Surakṣā forensic correlation | L |

## Epic 17: Surakṣā Navigation & Hub Integration
| Story | Title | Est. |
|-------|-------|------|
| [E17-S01](./E17-S01-suraksha-navigation.md) | NavGraph routes, Hub activation, badge, navigation wiring | M |

## Epic 18: Surakṣā E2E Testing
| Story | Title | Est. |
|-------|-------|------|
| [E18-S01](./E18-S01-suraksha-e2e.md) | End-to-end integration test suite (9 flow tests) | L |

---

**Surakṣā stories:** 10 stories across 7 epics  
**Estimated implementation:** ~50-60 hours  
**Combined total (PumpIQ + Surakṣā):** 37 stories, ~160-180 hours
