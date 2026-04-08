package com.ksetrasevakah.feature.pumpiq.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ksetrasevakah.core.common.Result
import com.ksetrasevakah.feature.pumpiq.chat.domain.model.ChatMessage
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.CreateChatThreadUseCase
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.GetChatMessagesUseCase
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.GetChatThreadsUseCase
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.SendChatMessageUseCase
import com.ksetrasevakah.feature.pumpiq.chat.model.ChatUiEvent
import com.ksetrasevakah.feature.pumpiq.chat.model.ChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatThreadsUseCase: GetChatThreadsUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val createChatThreadUseCase: CreateChatThreadUseCase,
    private val chatOrchestrator: ChatOrchestrator
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationTarget>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private var messagesJob: Job? = null

    init {
        observeThreads()
    }

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.SendMessage -> sendMessage(event.text)
            is ChatUiEvent.SelectThread -> selectThread(event.threadId)
            is ChatUiEvent.LoadInitialQuery -> handleInitialQuery(event.query)
            is ChatUiEvent.NavigateBack -> viewModelScope.launch {
                _navigationEvents.emit(NavigationTarget.Back)
            }
        }
    }

    private fun observeThreads() {
        viewModelScope.launch {
            getChatThreadsUseCase().collect { result ->
                when (result) {
                    is Result.Success -> _uiState.update {
                        it.copy(threads = result.data, isLoading = false)
                    }
                    is Result.Error -> _uiState.update {
                        it.copy(error = result.message, isLoading = false)
                    }
                    is Result.Loading -> _uiState.update {
                        it.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private fun selectThread(threadId: Long) {
        _uiState.update { it.copy(currentThreadId = threadId, messages = emptyList()) }
        observeMessages(threadId)
    }

    private fun observeMessages(threadId: Long) {
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            getChatMessagesUseCase(threadId).collect { result ->
                when (result) {
                    is Result.Success -> _uiState.update {
                        it.copy(messages = result.data)
                    }
                    is Result.Error -> _uiState.update {
                        it.copy(error = result.message)
                    }
                    is Result.Loading -> { /* no-op */ }
                }
            }
        }
    }

    private fun sendMessage(text: String) {
        viewModelScope.launch {
            val threadId = _uiState.value.currentThreadId ?: run {
                val result = createChatThreadUseCase(text.take(40))
                when (result) {
                    is Result.Success -> {
                        val newId = result.data
                        _uiState.update { it.copy(currentThreadId = newId) }
                        observeMessages(newId)
                        newId
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                        return@launch
                    }
                    is Result.Loading -> return@launch
                }
            }

            sendChatMessageUseCase(threadId, ChatMessage.ROLE_USER, text)
            _uiState.update { it.copy(isTyping = true) }

            val aiResponse = chatOrchestrator.getResponse(threadId, text)
            _uiState.update { it.copy(isTyping = false) }

            when (aiResponse) {
                is Result.Success -> {
                    sendChatMessageUseCase(threadId, ChatMessage.ROLE_ASSISTANT, aiResponse.data)
                }
                is Result.Error -> {
                    sendChatMessageUseCase(
                        threadId,
                        ChatMessage.ROLE_ASSISTANT,
                        "I encountered an error processing your request. Please try again."
                    )
                }
                is Result.Loading -> { /* no-op */ }
            }
        }
    }

    private fun handleInitialQuery(query: String) {
        if (query.isNotBlank()) {
            sendMessage(query)
        }
    }

    sealed interface NavigationTarget {
        data object Back : NavigationTarget
    }
}
