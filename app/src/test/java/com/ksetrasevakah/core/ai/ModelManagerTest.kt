package com.ksetrasevakah.core.ai

import android.util.Log
import com.ksetrasevakah.core.ai.model.ModelState
import com.ksetrasevakah.core.common.Constants
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ModelManagerTest {

    private lateinit var engine: MlcLlmEngine
    private lateinit var testScope: TestScope
    private lateinit var modelManager: ModelManager

    private val ingestionState = MutableStateFlow(ModelState.UNLOADED)
    private val orchestratorState = MutableStateFlow(ModelState.UNLOADED)

    @BeforeEach
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        engine = mockk(relaxed = true)
        every { engine.observeModelState(Constants.INGESTION_MODEL_ID) } returns ingestionState
        every { engine.observeModelState(Constants.ORCHESTRATOR_MODEL_ID) } returns orchestratorState
        testScope = TestScope()
        modelManager = ModelManager(engine, testScope)
    }

    @AfterEach
    fun teardown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `loadIngestionModel calls engine loadModel`() = testScope.runTest {
        modelManager.loadIngestionModel()
        advanceUntilIdle()

        coVerify { engine.loadModel(Constants.INGESTION_MODEL_ID) }
    }

    @Test
    fun `loadIngestionModel skips if already READY`() = testScope.runTest {
        ingestionState.value = ModelState.READY

        modelManager.loadIngestionModel()
        advanceUntilIdle()

        coVerify(exactly = 0) { engine.loadModel(Constants.INGESTION_MODEL_ID) }
    }

    @Test
    fun `loadOrchestratorModel calls engine loadModel`() = testScope.runTest {
        modelManager.loadOrchestratorModel()
        advanceUntilIdle()

        coVerify { engine.loadModel(Constants.ORCHESTRATOR_MODEL_ID) }
    }

    @Test
    fun `releaseOrchestratorIfIdle unloads after timeout`() = testScope.runTest {
        orchestratorState.value = ModelState.READY

        modelManager.releaseOrchestratorIfIdle()
        advanceTimeBy(ModelManager.ORCHESTRATOR_IDLE_TIMEOUT_MS + 1)

        coVerify { engine.unloadModel(Constants.ORCHESTRATOR_MODEL_ID) }
    }

    @Test
    fun `releaseOrchestratorIfIdle cancels previous idle job on new load`() = testScope.runTest {
        orchestratorState.value = ModelState.READY

        modelManager.releaseOrchestratorIfIdle()
        advanceTimeBy(ModelManager.ORCHESTRATOR_IDLE_TIMEOUT_MS / 2)

        modelManager.loadOrchestratorModel()
        advanceTimeBy(ModelManager.ORCHESTRATOR_IDLE_TIMEOUT_MS)

        coVerify(exactly = 0) { engine.unloadModel(Constants.ORCHESTRATOR_MODEL_ID) }
    }
}
