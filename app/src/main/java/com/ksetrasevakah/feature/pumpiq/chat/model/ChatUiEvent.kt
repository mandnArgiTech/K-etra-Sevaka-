package com.ksetrasevakah.feature.pumpiq.chat.model

sealed interface ChatUiEvent {
    data class SendMessage(val text: String) : ChatUiEvent
    data class SelectThread(val threadId: Long) : ChatUiEvent
    data class LoadInitialQuery(val query: String) : ChatUiEvent
    data object NavigateBack : ChatUiEvent
}
