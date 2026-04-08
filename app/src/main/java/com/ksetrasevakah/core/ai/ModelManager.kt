package com.ksetrasevakah.core.ai

import android.util.Log
import com.ksetrasevakah.core.ai.model.ModelState
import com.ksetrasevakah.core.common.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ModelManager @Inject constructor(
    private val engine: MlcLlmEngine,
    private val scope: CoroutineScope
) {

    private var orchestratorIdleJob: Job? = null

    fun loadIngestionModel() {
        val modelId = Constants.INGESTION_MODEL_ID
        scope.launch {
            val state = engine.observeModelState(modelId).value
            if (state != ModelState.READY && state != ModelState.LOADING) {
                try {
                    engine.loadModel(modelId)
                    Log.d(TAG, "Ingestion model loaded")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load ingestion model", e)
                }
            }
        }
    }

    fun loadOrchestratorModel() {
        val modelId = Constants.ORCHESTRATOR_MODEL_ID
        orchestratorIdleJob?.cancel()
        scope.launch {
            val state = engine.observeModelState(modelId).value
            if (state != ModelState.READY && state != ModelState.LOADING) {
                try {
                    engine.loadModel(modelId)
                    Log.d(TAG, "Orchestrator model loaded")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load orchestrator model", e)
                }
            }
        }
    }

    fun releaseOrchestratorIfIdle() {
        orchestratorIdleJob?.cancel()
        orchestratorIdleJob = scope.launch {
            delay(ORCHESTRATOR_IDLE_TIMEOUT_MS)
            val modelId = Constants.ORCHESTRATOR_MODEL_ID
            val state = engine.observeModelState(modelId).value
            if (state == ModelState.READY) {
                engine.unloadModel(modelId)
                Log.d(TAG, "Orchestrator model unloaded after idle timeout")
            }
        }
    }

    companion object {
        private const val TAG = "ModelManager"
        internal const val ORCHESTRATOR_IDLE_TIMEOUT_MS = 5 * 60 * 1000L // 5 minutes
    }
}
