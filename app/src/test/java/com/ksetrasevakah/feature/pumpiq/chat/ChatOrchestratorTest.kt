package com.ksetrasevakah.feature.pumpiq.chat

import com.ksetrasevakah.core.ai.MlcLlmEngine
import com.ksetrasevakah.core.ai.ModelManager
import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.domain.repository.ChatRepository
import com.ksetrasevakah.feature.pumpiq.chat.prompt.ContextAssembler
import com.ksetrasevakah.feature.pumpiq.chat.prompt.SystemPromptBuilder
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatOrchestratorTest {

    private lateinit var engine: MlcLlmEngine
    private lateinit var modelManager: ModelManager
    private lateinit var chatRepository: ChatRepository
    private lateinit var systemPromptBuilder: SystemPromptBuilder
    private lateinit var contextAssembler: ContextAssembler
    private lateinit var orchestrator: ChatOrchestrator

    @BeforeEach
    fun setup() {
        engine = mockk()
        modelManager = mockk(relaxed = true)
        chatRepository = mockk()
        systemPromptBuilder = mockk()
        contextAssembler = mockk()
        orchestrator = ChatOrchestrator(engine, modelManager, chatRepository, systemPromptBuilder, contextAssembler)
    }

    @Test
    fun `getResponse returns success with model output`() = runTest {
        coEvery { systemPromptBuilder.build(any()) } returns "System prompt"
        coEvery { contextAssembler.assemble(1) } returns "Previous context"
        every { engine.generate(any(), Constants.ORCHESTRATOR_MODEL_ID) } returns
            flowOf("The ", "motor ", "is ", "healthy.")

        val result = orchestrator.getResponse(1, "How is my motor?")

        assertTrue(result is Result.Success)
        assertEquals("The motor is healthy.", (result as Result.Success).data)
    }

    @Test
    fun `getResponse returns error when model returns blank`() = runTest {
        coEvery { systemPromptBuilder.build(any()) } returns "System prompt"
        coEvery { contextAssembler.assemble(1) } returns ""
        every { engine.generate(any(), Constants.ORCHESTRATOR_MODEL_ID) } returns flowOf("  ")

        val result = orchestrator.getResponse(1, "Test")

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getResponse returns error on exception`() = runTest {
        coEvery { systemPromptBuilder.build(any()) } returns "System prompt"
        coEvery { contextAssembler.assemble(1) } returns ""
        every { engine.generate(any(), Constants.ORCHESTRATOR_MODEL_ID) } returns flow {
            throw RuntimeException("Engine failure")
        }

        val result = orchestrator.getResponse(1, "Test")

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Engine failure"))
    }

    @Test
    fun `getResponse loads orchestrator model before generation`() = runTest {
        coEvery { systemPromptBuilder.build(any()) } returns "System prompt"
        coEvery { contextAssembler.assemble(1) } returns ""
        every { engine.generate(any(), any()) } returns flowOf("Response")

        orchestrator.getResponse(1, "Test")

        verify { modelManager.loadOrchestratorModel() }
    }

    @Test
    fun `getResponse releases model after successful generation`() = runTest {
        coEvery { systemPromptBuilder.build(any()) } returns "System prompt"
        coEvery { contextAssembler.assemble(1) } returns ""
        every { engine.generate(any(), any()) } returns flowOf("Response")

        orchestrator.getResponse(1, "Test")

        verify { modelManager.releaseOrchestratorIfIdle() }
    }
}
