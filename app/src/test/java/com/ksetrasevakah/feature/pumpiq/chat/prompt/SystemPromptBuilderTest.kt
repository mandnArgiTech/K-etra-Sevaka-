package com.ksetrasevakah.feature.pumpiq.chat.prompt

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.entity.PredictionCacheEntity
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.core.domain.repository.PredictionRepository
import com.ksetrasevakah.core.vectorstore.RagPipeline
import com.ksetrasevakah.designsystem.model.MotorState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SystemPromptBuilderTest {

    private lateinit var motorStateRepository: MotorStateRepository
    private lateinit var predictionRepository: PredictionRepository
    private lateinit var ragPipeline: RagPipeline
    private lateinit var builder: SystemPromptBuilder

    @BeforeEach
    fun setup() {
        motorStateRepository = mockk()
        predictionRepository = mockk()
        ragPipeline = mockk()
        builder = SystemPromptBuilder(motorStateRepository, predictionRepository, ragPipeline)
    }

    @Test
    fun `build includes PumpIQ identity`() = runTest {
        every { motorStateRepository.observeMotorState() } returns flowOf(Result.Success(MotorState.ON))
        coEvery { predictionRepository.getByType(any()) } returns Result.Success(null)

        val prompt = builder.build()

        assertTrue(prompt.contains("PumpIQ"))
    }

    @Test
    fun `build includes prediction info when available`() = runTest {
        every { motorStateRepository.observeMotorState() } returns flowOf(Result.Success(MotorState.ON))
        coEvery { predictionRepository.getByType("failure") } returns Result.Success(
            PredictionCacheEntity(
                type = "failure",
                resultJson = "{}",
                confidence = 0.85f,
                computedAt = System.currentTimeMillis(),
                validUntil = System.currentTimeMillis() + 86400000
            )
        )

        val prompt = builder.build()

        assertTrue(prompt.contains("failure"))
        assertTrue(prompt.contains("0.85"))
    }

    @Test
    fun `build handles prediction error gracefully`() = runTest {
        every { motorStateRepository.observeMotorState() } returns flowOf(Result.Success(MotorState.OFF))
        coEvery { predictionRepository.getByType(any()) } returns Result.Error("DB error")

        val prompt = builder.build()

        assertTrue(prompt.contains("Predictions unavailable"))
    }

    @Test
    fun `build includes farmer-friendly language instruction`() = runTest {
        every { motorStateRepository.observeMotorState() } returns flowOf(Result.Success(MotorState.OFF))
        coEvery { predictionRepository.getByType(any()) } returns Result.Success(null)

        val prompt = builder.build()

        assertTrue(prompt.contains("farmers"))
    }
}
