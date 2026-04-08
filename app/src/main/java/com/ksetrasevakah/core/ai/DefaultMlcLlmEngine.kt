package com.ksetrasevakah.core.ai

import android.util.Log
import com.ksetrasevakah.core.ai.model.ModelState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class DefaultMlcLlmEngine @Inject constructor() : MlcLlmEngine {

    private val stateFlows = mutableMapOf<String, MutableStateFlow<ModelState>>()
    private val mutex = Mutex()

    private fun getOrCreateStateFlow(modelId: String): MutableStateFlow<ModelState> =
        stateFlows.getOrPut(modelId) { MutableStateFlow(ModelState.UNLOADED) }

    override suspend fun loadModel(modelId: String) {
        mutex.withLock {
            val stateFlow = getOrCreateStateFlow(modelId)
            if (stateFlow.value == ModelState.READY) return
            stateFlow.value = ModelState.LOADING
            try {
                delay(SIMULATED_LOAD_MS)
                stateFlow.value = ModelState.READY
                Log.d(TAG, "Model $modelId loaded (stub)")
            } catch (e: Exception) {
                stateFlow.value = ModelState.ERROR
                throw e
            }
        }
    }

    override suspend fun unloadModel(modelId: String) {
        mutex.withLock {
            val stateFlow = getOrCreateStateFlow(modelId)
            stateFlow.value = ModelState.UNLOADED
            Log.d(TAG, "Model $modelId unloaded (stub)")
        }
    }

    override fun generate(prompt: String, modelId: String): Flow<String> = flow {
        mutex.withLock {
            val state = getOrCreateStateFlow(modelId).value
            if (state != ModelState.READY) {
                throw IllegalStateException("Model $modelId is not ready (current: $state)")
            }
        }
        val stubResponse = generateStubResponse(prompt)
        for (token in stubResponse.split(" ")) {
            delay(SIMULATED_TOKEN_DELAY_MS)
            emit("$token ")
        }
    }

    override fun observeModelState(modelId: String): StateFlow<ModelState> =
        getOrCreateStateFlow(modelId).asStateFlow()

    private fun generateStubResponse(prompt: String): String {
        if (prompt.contains("extract", ignoreCase = true) ||
            prompt.contains("SMS", ignoreCase = true)
        ) {
            return """{"motorOn":true,"phaseR":4.2,"phaseY":4.1,"phaseB":4.0,"voltage":230.0,"temperature":45.0,"runtimeMinutes":120}"""
        }
        if (prompt.contains("narrative", ignoreCase = true)) {
            return "Motor is running normally with balanced three-phase currents and stable voltage."
        }
        return "Stub response for prompt."
    }

    companion object {
        private const val TAG = "DefaultMlcLlmEngine"
        internal const val SIMULATED_LOAD_MS = 500L
        internal const val SIMULATED_TOKEN_DELAY_MS = 10L
    }
}
