# Technical Architecture Blueprint

## 1. System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        ANDROID APP                              │
│                                                                 │
│  ┌──────────┐   ┌──────────────┐   ┌──────────────────────┐   │
│  │   UI      │   │  ViewModel   │   │    Domain Layer      │   │
│  │ (Compose) │◄──│  (MVI)       │◄──│  UseCases            │   │
│  │           │──►│              │──►│                      │   │
│  └──────────┘   └──────────────┘   └──────────┬───────────┘   │
│                                                │               │
│                    ┌───────────────────────────┤               │
│                    │                           │               │
│  ┌────────────────┴──┐  ┌──────────────┐  ┌──┴─────────────┐ │
│  │   Data Layer       │  │  AI Engine   │  │  SMS Engine     │ │
│  │  ┌──────┐ ┌─────┐ │  │  ┌────────┐  │  │  ┌───────────┐ │ │
│  │  │Room  │ │FAISS│ │  │  │0.5B    │  │  │  │SmsManager │ │ │
│  │  │SQLite│ │Vec  │ │  │  │Ingest  │  │  │  │           │ │ │
│  │  └──────┘ └─────┘ │  │  ├────────┤  │  │  ├───────────┤ │ │
│  │  ┌──────────────┐ │  │  │3B      │  │  │  │Broadcast  │ │ │
│  │  │Google Drive  │ │  │  │Orch.   │  │  │  │Receiver   │ │ │
│  │  │Backup Worker │ │  │  └────────┘  │  │  └───────────┘ │ │
│  │  └──────────────┘ │  │  MLC-LLM     │  │                │ │
│  └───────────────────┘  └──────────────┘  └────────────────┘ │
│                                                               │
│                    SMS  ◄──────────────────►  Taro Panel      │
│                         070936 52065                          │
└───────────────────────────────────────────────────────────────┘
```

## 2. HybridAI Model Architecture

### 2.1 Ingestion Layer (0.5B Model)
- **Model:** Qwen2.5-0.5B-Instruct (q4f16_1 quantized via MLC-LLM)
- **Runs as:** Android Foreground Service (`IngestionService`)
- **Trigger:** SMS `BroadcastReceiver` filters messages from `070936 52065`
- **Pipeline:**
  1. Raw SMS text → 0.5B model extracts structured JSON
  2. JSON → Room database (hard data: timestamps, currents, voltage, faults)
  3. 0.5B generates narrative text → FAISS vector store (for RAG)
  4. Motor state updated in `MotorStateEntity` → UI observes via Flow

### 2.2 Orchestrator Layer (3B Model)
- **Model:** Qwen2.5-3B-Instruct (q4f16_1 quantized via MLC-LLM)
- **Runs:** On-demand when user opens AI Analyst or taps prediction
- **Context Management:**
  - Each chat thread has a `ChatThreadEntity` in Room
  - Last 10 messages loaded as conversation context
  - System prompt includes: current motor state, recent telemetry summary, prediction data
- **RAG Pipeline:**
  1. User query → embed via 0.5B model
  2. FAISS similarity search → top 5 narrative chunks
  3. Chunks + query + system prompt → 3B model → response
  4. Response + query saved to `ChatMessageEntity`

### 2.3 Model Lifecycle
```
App Start
  → Load 0.5B model into GPU/NPU memory (~300MB)
  → Start IngestionService
  → 3B model: lazy-loaded on first chat open (~1.5GB)
  → 3B model: unloaded after 5 min inactivity (memory pressure)
  → 0.5B model: always resident while app is alive
```

## 3. Two-Way SMS Loop

### 3.1 Outbound (App → Taro Panel)
```kotlin
// Command format sent to 070936 52065
"START"   → Start motor
"STOP"    → Stop motor  
"STATUS"  → Request current status
```

**Flow:**
1. User taps button → ViewModel dispatches `SendSmsCommand` UseCase
2. UseCase validates motor state (prevents duplicate START when already ON)
3. `SmsRepository.sendCommand(command)` → Android `SmsManager.sendTextMessage()`
4. `PendingIntent` for delivery confirmation registered
5. UI enters `PENDING` state
6. `SentReceiver` BroadcastReceiver confirms SMS was sent
7. Timer starts (30s timeout for reply)

### 3.2 Inbound (Taro Panel → App)
```
// Example SMS from Taro Panel
"MOTOR ON. R=3.82A Y=3.71A B=3.89A V=228V TEMP=42C"
"MOTOR OFF. RUNTIME=4H22M. NO FAULTS."
"ALERT: DRY RUN DETECTED. MOTOR STOPPED."
"STATUS: MOTOR ON. UPTIME=6H14M. R=3.80A Y=3.68A B=3.91A V=225V"
```

**Flow:**
1. `SmsBroadcastReceiver` intercepts ALL incoming SMS
2. **Critical filter:** `if (sender != "070936 52065") return` — ignore all other SMS
3. Raw SMS text → `IngestionService` via Intent
4. 0.5B model parses → structured `TelemetryEntity` saved to Room
5. Motor state entity updated → Dashboard UI auto-refreshes via Flow
6. If this was a response to START/STOP, pending state resolves
7. Narrative text generated → FAISS vector store

## 4. Data Persistence

### 4.1 Room Database (SQLite)
```
Tables:
├── motor_state          # Current motor state (singleton row)
├── telemetry_log        # Every SMS parsed into structured data
├── fault_log            # Extracted fault events
├── worker_activity      # ON/OFF timestamps per day
├── prediction_cache     # Cached prediction results
├── chat_threads         # Chat thread metadata
├── chat_messages        # Individual messages per thread
└── backup_log           # Backup history
```

### 4.2 Local Vector Store (FAISS)
- Stores narrative text embeddings generated by 0.5B model
- Index file persisted at `app_data/vector_store/faiss.index`
- Metadata JSON alongside for chunk → source mapping
- Rebuilt on backup restore

### 4.3 Google Drive Backup
- **WorkManager** scheduled job: daily at 02:00 AM
- Manual trigger from Settings
- **Backup contents:** Room DB export, FAISS index + metadata, chat threads
- **Restore:** Import from Drive → replace local DB → rebuild FAISS index
- Uses Google Drive REST API v3 with OAuth2

## 5. Prediction Engine

### 5.1 Power Failure Prediction
- **Input:** 14-day voltage readings from telemetry_log
- **Method:** Time-series pattern matching on hourly voltage averages
- **Output:** Predicted time + voltage + confidence percentage
- **Update frequency:** Every new telemetry SMS

### 5.2 Fault Prediction  
- **Input:** Fault history, phase current trends, runtime duration
- **Method:** Sliding window fault frequency + current drift detection
- **Output:** Risk timeline (0h to +12h) with fault type probability
- **Triggers:** B-phase > 4.5A = imminent dry run flag

### 5.3 Worker Behavior Prediction
- **Input:** worker_activity table (14-day ON/OFF log)
- **Method:** Statistical mean/std of ON times, day-of-week grouping
- **Output:** Predicted next ON time + confidence + forgot-OFF risk %
- **Forgot-OFF detection:** If motor ON past (avg_off_time + 45min) → alert

### 5.4 Prediction Data Flow
```
telemetry_log ─┐
fault_log ─────┤
worker_activity┤───► PredictionEngine ───► prediction_cache
motor_state ───┘         │                      │
                         │                      ▼
                    3B model prompt         Dashboard UI
                    enrichment             Prediction Cards
```
