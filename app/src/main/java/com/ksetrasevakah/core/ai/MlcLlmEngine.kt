package com.ksetrasevakah.core.ai

import com.ksetrasevakah.core.ai.model.ModelState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MlcLlmEngine {
    suspend fun loadModel(modelId: String)
    suspend fun unloadModel(modelId: String)
    fun generate(prompt: String, modelId: String): Flow<String>
    fun observeModelState(modelId: String): StateFlow<ModelState>
}
