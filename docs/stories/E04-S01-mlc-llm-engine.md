# E04-S01: MLC-LLM Engine Wrapper & Model Lifecycle

**Epic:** 04 — AI Ingestion Layer  
**Size:** L (4-8h)  
**Dependencies:** E01-S01

## Description
Create the MLC-LLM engine wrapper that manages loading/unloading of the 0.5B and 3B models with proper GPU/NPU memory management on Snapdragon 8s Gen 3.

## Acceptance Criteria
- [ ] AC1: `MlcLlmEngine` singleton wraps MLC-LLM Android SDK
- [ ] AC2: `loadModel(modelId: String)` loads specified model to GPU memory
- [ ] AC3: `unloadModel(modelId: String)` frees GPU memory
- [ ] AC4: `generate(prompt: String, modelId: String): Flow<String>` streams tokens
- [ ] AC5: 0.5B model auto-loaded on app start and kept resident
- [ ] AC6: 3B model lazy-loaded on first chat, unloaded after 5 min idle
- [ ] AC7: `ModelState` exposed as `StateFlow` (UNLOADED, LOADING, READY, ERROR)
- [ ] AC8: Concurrent generation requests queued (not parallel — GPU contention)
- [ ] AC9: Unit tests with mocked MLC-LLM SDK pass

## Files to Create
```
app/src/main/java/com/ksetrasevakah/core/ai/
├── MlcLlmEngine.kt                  # Engine wrapper
├── ModelManager.kt                   # Lifecycle management (load/unload/idle timeout)
├── model/ModelState.kt              # UNLOADED, LOADING, READY, ERROR
├── model/GenerationRequest.kt       # Prompt + modelId + callback
└── di/AiModule.kt                   # Hilt provider

app/src/test/java/com/ksetrasevakah/core/ai/ModelManagerTest.kt
app/src/test/java/com/ksetrasevakah/core/ai/MlcLlmEngineTest.kt
```

## Key Implementation Notes
- MLC-LLM Android uses `MLCEngine` class from `ai.mlc.mlcllm`
- Models stored in `app/src/main/assets/models/` or downloaded on first run
- GPU memory ~300MB for 0.5B, ~1.5GB for 3B (q4f16_1 quantization)
- Snapdragon 8s Gen 3 has Adreno 735 GPU — use OpenCL backend

## Test Requirements
### ModelManagerTest
- Load 0.5B → state transitions: UNLOADED → LOADING → READY
- Idle timeout on 3B → state transitions: READY → UNLOADED after 5 min
- Concurrent load requests → queued, not parallel
- Error during load → state = ERROR with message

### MlcLlmEngineTest
- generate() with loaded model → emits tokens
- generate() with unloaded model → auto-triggers load first
- generate() returns complete response string

## Definition of Done
- Engine wrapper compiles and provides DI
- Model lifecycle managed correctly
- All tests pass
