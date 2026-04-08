package com.ksetrasevakah.feature.pumpiq.chat

import app.cash.turbine.test
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.pumpiq.chat.domain.model.ChatMessage
import com.ksetrasevakah.feature.pumpiq.chat.domain.model.ChatThread
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.CreateChatThreadUseCase
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.GetChatMessagesUseCase
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.GetChatThreadsUseCase
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.SendChatMessageUseCase
import com.ksetrasevakah.feature.pumpiq.chat.model.ChatUiEvent
import io.mockk.coEvery
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getChatThreadsUseCase: GetChatThreadsUseCase
    private lateinit var getChatMessagesUseCase: GetChatMessagesUseCase
    private lateinit var sendChatMessageUseCase: SendChatMessageUseCase
    private lateinit var createChatThreadUseCase: CreateChatThreadUseCase
    private lateinit var chatOrchestrator: ChatOrchestrator

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getChatThreadsUseCase = mockk()
        getChatMessagesUseCase = mockk()
        sendChatMessageUseCase = mockk()
        createChatThreadUseCase = mockk()
        chatOrchestrator = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): ChatViewModel {
        every { getChatThreadsUseCase() } returns flowOf(Result.Success(emptyList()))
        return ChatViewModel(
            getChatThreadsUseCase,
            getChatMessagesUseCase,
            sendChatMessageUseCase,
            createChatThreadUseCase,
            chatOrchestrator
        )
    }

    @Test
    fun `initial state has empty messages and loading false after threads load`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.messages.isEmpty())
        assertNull(state.currentThreadId)
    }

    @Test
    fun `threads are loaded on init`() = runTest {
        val threads = listOf(
            ChatThread(1, "Thread 1", "Preview", 1000L, 2000L),
            ChatThread(2, "Thread 2", "Preview 2", 1000L, 3000L)
        )
        every { getChatThreadsUseCase() } returns flowOf(Result.Success(threads))

        val vm = ChatViewModel(
            getChatThreadsUseCase, getChatMessagesUseCase,
            sendChatMessageUseCase, createChatThreadUseCase, chatOrchestrator
        )
        advanceUntilIdle()

        assertEquals(2, vm.uiState.value.threads.size)
    }

    @Test
    fun `select thread updates currentThreadId and observes messages`() = runTest {
        val messages = listOf(
            ChatMessage(1, 1, "user", "Hello", 1000L)
        )
        every { getChatThreadsUseCase() } returns flowOf(Result.Success(emptyList()))
        every { getChatMessagesUseCase(1) } returns flowOf(Result.Success(messages))

        val vm = ChatViewModel(
            getChatThreadsUseCase, getChatMessagesUseCase,
            sendChatMessageUseCase, createChatThreadUseCase, chatOrchestrator
        )
        advanceUntilIdle()

        vm.onEvent(ChatUiEvent.SelectThread(1))
        advanceUntilIdle()

        assertEquals(1L, vm.uiState.value.currentThreadId)
        assertEquals(1, vm.uiState.value.messages.size)
    }

    @Test
    fun `send message creates thread when no current thread`() = runTest {
        every { getChatThreadsUseCase() } returns flowOf(Result.Success(emptyList()))
        coEvery { createChatThreadUseCase(any()) } returns Result.Success(42L)
        every { getChatMessagesUseCase(42) } returns flowOf(Result.Success(emptyList()))
        coEvery { sendChatMessageUseCase(42, any(), any()) } returns Result.Success(1L)
        coEvery { chatOrchestrator.getResponse(42, any()) } returns Result.Success("AI response")

        val vm = ChatViewModel(
            getChatThreadsUseCase, getChatMessagesUseCase,
            sendChatMessageUseCase, createChatThreadUseCase, chatOrchestrator
        )
        advanceUntilIdle()

        vm.onEvent(ChatUiEvent.SendMessage("Hello"))
        advanceUntilIdle()

        assertEquals(42L, vm.uiState.value.currentThreadId)
        assertFalse(vm.uiState.value.isTyping)
    }

    @Test
    fun `error from threads use case sets error state`() = runTest {
        every { getChatThreadsUseCase() } returns flowOf(Result.Error("Network error"))

        val vm = ChatViewModel(
            getChatThreadsUseCase, getChatMessagesUseCase,
            sendChatMessageUseCase, createChatThreadUseCase, chatOrchestrator
        )
        advanceUntilIdle()

        assertEquals("Network error", vm.uiState.value.error)
    }

    @Test
    fun `navigate back emits navigation event`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.navigationEvents.test {
            vm.onEvent(ChatUiEvent.NavigateBack)
            assertEquals(ChatViewModel.NavigationTarget.Back, awaitItem())
        }
    }
}
