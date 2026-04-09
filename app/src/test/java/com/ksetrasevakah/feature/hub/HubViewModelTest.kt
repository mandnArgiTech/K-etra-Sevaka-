package com.ksetrasevakah.feature.hub

import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HubViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SecurityEventRepository

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState reflects unacknowledged high count from repository flow`() = runTest(testDispatcher) {
        every { repository.getUnacknowledgedHighCount() } returns flowOf(Result.Success(4))

        val vm = HubViewModel(repository)
        advanceUntilIdle()

        assertEquals(4, vm.uiState.value.unacknowledgedHighAlerts)
    }
}
