package com.ksetrasevakah.feature.suraksha.dashboard

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.dashboard.model.SurakshaDashboardUiEvent
import com.ksetrasevakah.feature.suraksha.domain.model.EventType
import com.ksetrasevakah.feature.suraksha.domain.model.SecurityEvent
import com.ksetrasevakah.feature.suraksha.domain.model.ThreatLevel
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import com.ksetrasevakah.feature.suraksha.prediction.SecurityBriefingGenerator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SurakshaDashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var securityEventRepository: SecurityEventRepository
    private lateinit var briefingGenerator: SecurityBriefingGenerator

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        securityEventRepository = mockk()
        briefingGenerator = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun stubDefaults() {
        coEvery { securityEventRepository.getThreatCounts() } returns Result.Success(emptyMap())
        every { securityEventRepository.getUnacknowledgedCount() } returns flowOf(Result.Success(0))
        every { securityEventRepository.observeEvents(any()) } returns flowOf(Result.Success(emptyList()))
        coEvery { briefingGenerator.generate() } returns Result.Success("All clear.")
    }

    private fun createViewModel(): SurakshaDashboardViewModel {
        stubDefaults()
        return SurakshaDashboardViewModel(securityEventRepository, briefingGenerator)
    }

    @Test
    fun `initial state loads threat counts successfully`() = runTest {
        val counts = mapOf(ThreatLevel.CRITICAL to 2, ThreatLevel.HIGH to 5)
        coEvery { securityEventRepository.getThreatCounts() } returns Result.Success(counts)
        every { securityEventRepository.getUnacknowledgedCount() } returns flowOf(Result.Success(3))
        every { securityEventRepository.observeEvents(any()) } returns flowOf(Result.Success(emptyList()))
        coEvery { briefingGenerator.generate() } returns Result.Success("Briefing text")

        val vm = SurakshaDashboardViewModel(securityEventRepository, briefingGenerator)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.threatCounts[ThreatLevel.CRITICAL])
        assertEquals(5, state.threatCounts[ThreatLevel.HIGH])
        assertEquals(3, state.unacknowledgedCount)
    }

    @Test
    fun `events are loaded and heatmap is built`() = runTest {
        val now = System.currentTimeMillis()
        val events = listOf(
            SecurityEvent(
                id = 1L,
                cameraName = "cam1",
                eventType = EventType.PERSON,
                threatLevel = ThreatLevel.MEDIUM,
                confidence = 0.8f,
                originTimestamp = now,
                receivedTimestamp = now,
                hourOfDay = 14,
                description = "Person at gate"
            )
        )
        coEvery { securityEventRepository.getThreatCounts() } returns Result.Success(emptyMap())
        every { securityEventRepository.getUnacknowledgedCount() } returns flowOf(Result.Success(1))
        every { securityEventRepository.observeEvents(any()) } returns flowOf(Result.Success(events))
        coEvery { briefingGenerator.generate() } returns Result.Success("")

        val vm = SurakshaDashboardViewModel(securityEventRepository, briefingGenerator)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(1, state.ledgerEvents.size)
        assertTrue(state.heatmapData.isNotEmpty())
    }

    @Test
    fun `error from repository is reflected in UI state`() = runTest {
        coEvery { securityEventRepository.getThreatCounts() } returns Result.Error("Network failure")
        every { securityEventRepository.getUnacknowledgedCount() } returns flowOf(Result.Success(0))
        every { securityEventRepository.observeEvents(any()) } returns flowOf(Result.Success(emptyList()))
        coEvery { briefingGenerator.generate() } returns Result.Success("")

        val vm = SurakshaDashboardViewModel(securityEventRepository, briefingGenerator)
        advanceUntilIdle()

        assertEquals("Network failure", vm.uiState.value.error)
    }

    @Test
    fun `acknowledge event calls repository`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        coEvery { securityEventRepository.acknowledgeEvent(1L) } returns Result.Success(Unit)

        vm.onEvent(SurakshaDashboardUiEvent.AcknowledgeEvent(1L))
        advanceUntilIdle()

        coVerify { securityEventRepository.acknowledgeEvent(1L) }
    }

    @Test
    fun `briefing is loaded into state`() = runTest {
        coEvery { securityEventRepository.getThreatCounts() } returns Result.Success(emptyMap())
        every { securityEventRepository.getUnacknowledgedCount() } returns flowOf(Result.Success(0))
        every { securityEventRepository.observeEvents(any()) } returns flowOf(Result.Success(emptyList()))
        coEvery { briefingGenerator.generate() } returns Result.Success("Farm is secure today.")

        val vm = SurakshaDashboardViewModel(securityEventRepository, briefingGenerator)
        advanceUntilIdle()

        assertEquals("Farm is secure today.", vm.uiState.value.briefing)
    }

    @Test
    fun `retry reloads the dashboard`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onEvent(SurakshaDashboardUiEvent.Retry)
        advanceUntilIdle()

        coVerify(atLeast = 2) { securityEventRepository.getThreatCounts() }
    }
}
