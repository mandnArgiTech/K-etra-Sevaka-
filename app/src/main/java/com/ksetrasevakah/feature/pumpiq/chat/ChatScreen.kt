package com.ksetrasevakah.feature.pumpiq.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ksetrasevakah.designsystem.theme.KsetraDarkBackground
import com.ksetrasevakah.designsystem.theme.KsetraSpacing
import com.ksetrasevakah.designsystem.theme.KsetraSurface
import com.ksetrasevakah.designsystem.theme.KsetraTextPrimary
import com.ksetrasevakah.designsystem.theme.KsetraTextSecondary
import com.ksetrasevakah.feature.pumpiq.chat.component.ChatInputBar
import com.ksetrasevakah.feature.pumpiq.chat.component.MessageBubble
import com.ksetrasevakah.feature.pumpiq.chat.component.QuickQueryChips
import com.ksetrasevakah.feature.pumpiq.chat.component.ThreadDrawer
import com.ksetrasevakah.feature.pumpiq.chat.component.TypingIndicator
import com.ksetrasevakah.feature.pumpiq.chat.domain.model.ChatMessage
import com.ksetrasevakah.feature.pumpiq.chat.model.ChatUiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    initialQuery: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LaunchedEffect(initialQuery) {
        if (!initialQuery.isNullOrBlank()) {
            viewModel.onEvent(ChatUiEvent.LoadInitialQuery(initialQuery))
        }
    }

    LaunchedEffect(viewModel.navigationEvents) {
        viewModel.navigationEvents.collect { target ->
            when (target) {
                is ChatViewModel.NavigationTarget.Back -> onNavigateBack()
            }
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ThreadDrawer(
                threads = uiState.threads,
                currentThreadId = uiState.currentThreadId,
                onThreadSelected = { threadId ->
                    viewModel.onEvent(ChatUiEvent.SelectThread(threadId))
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KsetraDarkBackground)
        ) {
            TopAppBar(
                title = { Text("PumpIQ Chat", color = KsetraTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(ChatUiEvent.NavigateBack) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = KsetraTextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Chat history",
                            tint = KsetraTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KsetraSurface
                )
            )

            Box(modifier = Modifier.weight(1f)) {
                if (uiState.messages.isEmpty() && !uiState.isTyping) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(KsetraSpacing.screenPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ask PumpIQ anything about your motor",
                            color = KsetraTextSecondary,
                            modifier = Modifier.padding(bottom = KsetraSpacing.lg)
                        )
                        QuickQueryChips(
                            onChipSelected = { query ->
                                viewModel.onEvent(ChatUiEvent.SendMessage(query))
                            }
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = KsetraSpacing.screenPadding,
                            vertical = KsetraSpacing.sm
                        ),
                        verticalArrangement = Arrangement.spacedBy(KsetraSpacing.sm)
                    ) {
                        items(uiState.messages, key = { it.id }) { message ->
                            MessageBubble(
                                text = message.text,
                                isUser = message.role == ChatMessage.ROLE_USER,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (uiState.isTyping) {
                            item {
                                TypingIndicator(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }

            ChatInputBar(
                onSendMessage = { text ->
                    viewModel.onEvent(ChatUiEvent.SendMessage(text))
                },
                isEnabled = !uiState.isTyping,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
