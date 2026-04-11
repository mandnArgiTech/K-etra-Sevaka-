package com.ksetrasevakah.core.ai

import android.content.Context
import android.util.Log
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.ksetrasevakah.core.ai.model.ModelState
import com.ksetrasevakah.core.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device LLM inference via LiteRT-LM.
 *
 * Uses [Backend.GPU] which routes through OpenCL on Adreno GPUs (Snapdragon 8s Gen 3 and similar).
 * Falls back to CPU automatically if GPU is unavailable on the device.
 * The model file must be in `.litertlm` format under [Context.getFilesDir]/models/.
 */
@Singleton
class LiteRtLmEngine @Inject constructor(
    @ApplicationContext private val context: Context
) : MlcLlmEngine {

    private val mutex = Mutex()
    private var engine: Engine? = null
    private val _modelState = MutableStateFlow(ModelState.UNLOADED)

    override suspend fun loadModel(modelId: String): Unit = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (_modelState.value == ModelState.READY) return@withLock
            _modelState.value = ModelState.LOADING

            val modelFile = File(context.filesDir, "models/${Constants.ORCHESTRATOR_MODEL_FILENAME}")
            if (!modelFile.exists() || modelFile.length() < MIN_MODEL_BYTES) {
                _modelState.value = ModelState.ERROR
                throw IllegalStateException(
                    "Model file missing or too small: ${modelFile.absolutePath}. Run setup download first."
                )
            }

            try {
                engine?.close()
                val cfg = EngineConfig(
                    modelPath = modelFile.absolutePath,
                    backend   = Backend.GPU(),
                    cacheDir  = context.cacheDir.absolutePath
                )
                val e = Engine(cfg)
                e.initialize()
                engine = e
                _modelState.value = ModelState.READY
                Log.i(TAG, "LiteRT-LM ready — GPU backend (OpenCL/Adreno), model: ${modelFile.name}")
            } catch (ex: Exception) {
                _modelState.value = ModelState.ERROR
                Log.e(TAG, "Failed to initialise LiteRT-LM engine", ex)
                throw ex
            }
        }
    }

    override suspend fun unloadModel(modelId: String) {
        mutex.withLock {
            engine?.close()
            engine = null
            _modelState.value = ModelState.UNLOADED
            Log.d(TAG, "LiteRT-LM engine unloaded")
        }
    }

    /**
     * Generates a streamed response for [prompt].
     * Each emission is a partial text chunk as the GPU produces tokens.
     * [modelId] is ignored — only the single orchestrator model is loaded.
     */
    override fun generate(prompt: String, modelId: String): Flow<String> = flow {
        val e = mutex.withLock {
            if (_modelState.value != ModelState.READY) {
                throw IllegalStateException("LiteRT-LM engine is not ready (state: ${_modelState.value})")
            }
            requireNotNull(engine)
        }

        e.createConversation().use { conversation ->
            conversation.sendMessageAsync(prompt)
                .map { it.toString() }
                .collect { chunk -> emit(chunk) }
        }
    }

    override fun observeModelState(modelId: String): StateFlow<ModelState> =
        _modelState.asStateFlow()

    companion object {
        private const val TAG = "LiteRtLmEngine"
        private const val MIN_MODEL_BYTES = 1_000_000L
    }
}
