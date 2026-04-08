package com.ksetrasevakah.feature.pumpiq.chat;

import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.CreateChatThreadUseCase;
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.GetChatMessagesUseCase;
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.GetChatThreadsUseCase;
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.SendChatMessageUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<GetChatThreadsUseCase> getChatThreadsUseCaseProvider;

  private final Provider<GetChatMessagesUseCase> getChatMessagesUseCaseProvider;

  private final Provider<SendChatMessageUseCase> sendChatMessageUseCaseProvider;

  private final Provider<CreateChatThreadUseCase> createChatThreadUseCaseProvider;

  private final Provider<ChatOrchestrator> chatOrchestratorProvider;

  public ChatViewModel_Factory(Provider<GetChatThreadsUseCase> getChatThreadsUseCaseProvider,
      Provider<GetChatMessagesUseCase> getChatMessagesUseCaseProvider,
      Provider<SendChatMessageUseCase> sendChatMessageUseCaseProvider,
      Provider<CreateChatThreadUseCase> createChatThreadUseCaseProvider,
      Provider<ChatOrchestrator> chatOrchestratorProvider) {
    this.getChatThreadsUseCaseProvider = getChatThreadsUseCaseProvider;
    this.getChatMessagesUseCaseProvider = getChatMessagesUseCaseProvider;
    this.sendChatMessageUseCaseProvider = sendChatMessageUseCaseProvider;
    this.createChatThreadUseCaseProvider = createChatThreadUseCaseProvider;
    this.chatOrchestratorProvider = chatOrchestratorProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(getChatThreadsUseCaseProvider.get(), getChatMessagesUseCaseProvider.get(), sendChatMessageUseCaseProvider.get(), createChatThreadUseCaseProvider.get(), chatOrchestratorProvider.get());
  }

  public static ChatViewModel_Factory create(
      Provider<GetChatThreadsUseCase> getChatThreadsUseCaseProvider,
      Provider<GetChatMessagesUseCase> getChatMessagesUseCaseProvider,
      Provider<SendChatMessageUseCase> sendChatMessageUseCaseProvider,
      Provider<CreateChatThreadUseCase> createChatThreadUseCaseProvider,
      Provider<ChatOrchestrator> chatOrchestratorProvider) {
    return new ChatViewModel_Factory(getChatThreadsUseCaseProvider, getChatMessagesUseCaseProvider, sendChatMessageUseCaseProvider, createChatThreadUseCaseProvider, chatOrchestratorProvider);
  }

  public static ChatViewModel newInstance(GetChatThreadsUseCase getChatThreadsUseCase,
      GetChatMessagesUseCase getChatMessagesUseCase, SendChatMessageUseCase sendChatMessageUseCase,
      CreateChatThreadUseCase createChatThreadUseCase, ChatOrchestrator chatOrchestrator) {
    return new ChatViewModel(getChatThreadsUseCase, getChatMessagesUseCase, sendChatMessageUseCase, createChatThreadUseCase, chatOrchestrator);
  }
}
