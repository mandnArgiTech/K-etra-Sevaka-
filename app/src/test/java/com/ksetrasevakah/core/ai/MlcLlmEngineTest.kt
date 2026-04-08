package com.ksetrasevakah.core.ai

import android.util.Log
import com.ksetrasevakah.core.ai.model.ModelState
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@OptIn(ExperimentalCoroutinesApi::class)
class MlcLlmEngineTest {

    private lateinit var engine: DefaultMlcLlmEngine

    @BeforeEach
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        engine = DefaultMlcLlmEngine()
    }

    @AfterEach
    fun teardown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `loadModel transitions state from UNLOADED to READY`() = runTest {
        val modelId = "test-model"
        assertEquals(ModelState.UNLOADED, engine.observeModelState(modelId).value)

        engine.loadModel(modelId)

        assertEquals(ModelState.READY, engine.observeModelState(modelId).value)
    }

    @Test
    fun `unloadModel transitions state back to UNLOADED`() = runTest {
        val modelId = "test-model"
        engine.loadModel(modelId)
        assertEquals(ModelState.READY, engine.observeModelState(modelId).value)

        engine.unloadModel(modelId)

        assertEquals(ModelState.UNLOADED, engine.observeModelState(modelId).value)
    }

    @Test
    fun `generate emits tokens when model is ready`() = runTest {
        val modelId = "test-model"
        engine.loadModel(modelId)

        val tokens = engine.generate("extract SMS data", modelId).toList()

        assertTrue(tokens.isNotEmpty())
        val fullResponse = tokens.joinToString("")
        assertTrue(fullResponse.contains("motorOn"))
    }

    @Test
    fun `generate throws when model is not ready`() = runTest {
        val modelId = "test-model"

        assertThrows<IllegalStateException> {
            engine.generate("test prompt", modelId).toList()
        }
    }
}
