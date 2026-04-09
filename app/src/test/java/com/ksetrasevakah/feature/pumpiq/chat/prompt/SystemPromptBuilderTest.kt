package com.ksetrasevakah.feature.pumpiq.chat.prompt

import com.ksetrasevakah.core.common.Constants
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.core.database.dao.SecurityBriefingDao
import com.ksetrasevakah.core.database.entity.PredictionCacheEntity
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.core.domain.repository.PredictionRepository
import com.ksetrasevakah.core.vectorstore.RagPipeline
import com.ksetrasevakah.designsystem.model.MotorState
import com.ksetrasevakah.feature.suraksha.domain.model.CameraConfig
import com.ksetrasevakah.feature.suraksha.domain.model.CameraMode
import com.ksetrasevakah.feature.suraksha.domain.model.EventType
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import com.ksetrasevakah.feature.suraksha.prediction.CrossModuleCorrelator
import com.ksetrasevakah.feature.suraksha.prediction.CrossModuleCorrelation
import com.ksetrasevakah.designsystem.model.RiskLevel
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
    private lateinit var cameraConfigRepository: CameraConfigRepository
    private lateinit var securityEventRepository: SecurityEventRepository
    private lateinit var securityBriefingDao: SecurityBriefingDao
    private lateinit var crossModuleCorrelator: CrossModuleCorrelator
    private lateinit var builder: SystemPromptBuilder

    @BeforeEach
    fun setup() {
        motorStateRepository = mockk()
        predictionRepository = mockk()
        ragPipeline = mockk()
        cameraConfigRepository = mockk()
        securityEventRepository = mockk()
        securityBriefingDao = mockk()
        crossModuleCorrelator = mockk()
        every { motorStateRepository.observeMotorState() } returns flowOf(Result.Success(MotorState.ON))
        coEvery { predictionRepository.getByType(any()) } returns Result.Success(null)
        every { cameraConfigRepository.observeAllCameras() } returns flowOf(Result.Success(emptyList()))
        coEvery { securityEventRepository.getThreatDistribution(any()) } returns Result.Success(emptyList())
        coEvery { securityEventRepository.getLatestEvent() } returns Result.Success(null)
        coEvery { securityBriefingDao.getRecent(any()) } returns emptyList()
        coEvery { crossModuleCorrelator.findCrossModuleCorrelations(any(), any()) } returns Result.Success(emptyList())
        builder = SystemPromptBuilder(
            motorStateRepository,
            predictionRepository,
            ragPipeline,
            cameraConfigRepository,
            securityEventRepository,
            securityBriefingDao,
            crossModuleCorrelator
        )
    }

    @Test
    fun `build includes PumpIQ identity`() = runTest {
        val prompt = builder.build()
        assertTrue(prompt.contains("PumpIQ"))
    }

    @Test
    fun `build includes Suraksha module section`() = runTest {
        val prompt = builder.build()
        assertTrue(prompt.contains("[MODULE: Surakṣā]"))
        assertTrue(prompt.contains("No security events recorded"))
    }

    @Test
    fun `build includes prediction info when available`() = runTest {
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
        coEvery { predictionRepository.getByType(any()) } returns Result.Error("DB error")

        val prompt = builder.build()

        assertTrue(prompt.contains("Predictions unavailable"))
    }

    @Test
    fun `build includes farmer-friendly language instruction`() = runTest {
        val prompt = builder.build()
        assertTrue(prompt.contains("farmers"))
    }

    @Test
    fun `build queries RAG with PumpIQ and Suraksha namespaces`() = runTest {
        coEvery {
            ragPipeline.query(
                queryText = "motion",
                topK = Constants.VECTOR_SEARCH_TOP_K,
                namespaces = setOf(Constants.RAG_MODULE_PUMPIQ, Constants.RAG_MODULE_SURAKSHA)
            )
        } returns Result.Success("ctx")

        val prompt = builder.build("motion")

        assertTrue(prompt.contains("ctx"))
    }

    @Test
    fun `build includes cross-module correlations when present`() = runTest {
        val corr = CrossModuleCorrelation(
            securityEvent = SecurityEvent(
                cameraName = "A",
                eventType = EventType.PERSON,
                threatLevel = ThreatLevel.HIGH,
                confidence = 0.8f,
                originTimestamp = 1L,
                receivedTimestamp = 1L,
                hourOfDay = 22
            ),
            pumpIqEventDescription = "power",
            pumpIqTimestamp = 2L,
            timeDeltaMs = 1L,
            correlationType = "PERSON_NEAR_POWER_FAILURE",
            severity = RiskLevel.HIGH,
            description = "Person at A — power issue"
        )
        coEvery { crossModuleCorrelator.findCrossModuleCorrelations(any(), any()) } returns
            Result.Success(listOf(corr))

        val prompt = builder.build()

        assertTrue(prompt.contains("CROSS-MODULE CORRELATIONS"))
        assertTrue(prompt.contains("PERSON_NEAR_POWER_FAILURE"))
    }

    @Test
    fun `build notes when cross-module correlator fails`() = runTest {
        coEvery { crossModuleCorrelator.findCrossModuleCorrelations(any(), any()) } returns
            Result.Error("db down")

        val prompt = builder.build()

        assertTrue(prompt.contains("Cross-module correlations unavailable"))
    }

    @Test
    fun `build shows latest unavailable when threat distribution and latest both fail`() = runTest {
        coEvery { securityEventRepository.getThreatDistribution(any()) } returns Result.Error("dist")
        coEvery { securityEventRepository.getLatestEvent() } returns Result.Error("latest")

        val prompt = builder.build()

        assertTrue(prompt.contains("Threat summaries unavailable"))
        assertTrue(prompt.contains("Latest event: unavailable."))
    }

    @Test
    fun `build shows camera count when configs exist`() = runTest {
        val cams = listOf(
            CameraConfig(
                id = 1L,
                cameraName = "C1",
                mode = CameraMode.ACTIVE,
                lastSeen = 1L,
                createdAt = 1L
            )
        )
        every { cameraConfigRepository.observeAllCameras() } returns flowOf(Result.Success(cams))

        val prompt = builder.build()

        assertTrue(prompt.contains("Active cameras (configured): 1"))
    }
}
