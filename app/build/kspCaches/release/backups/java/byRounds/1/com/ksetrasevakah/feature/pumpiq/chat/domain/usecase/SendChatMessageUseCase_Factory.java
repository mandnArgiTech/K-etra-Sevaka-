package com.ksetrasevakah.feature.pumpiq.chat.domain.usecase;

import com.ksetrasevakah.core.domain.repository.ChatRepository;
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
public final class SendChatMessageUseCase_Factory implements Factory<SendChatMessageUseCase> {
  private final Provider<ChatRepository> chatRepositoryProvider;

  public SendChatMessageUseCase_Factory(Provider<ChatRepository> chatRepositoryProvider) {
    this.chatRepositoryProvider = chatRepositoryProvider;
  }

  @Override
  public SendChatMessageUseCase get() {
    return newInstance(chatRepositoryProvider.get());
  }

  public static SendChatMessageUseCase_Factory create(
      Provider<ChatRepository> chatRepositoryProvider) {
    return new SendChatMessageUseCase_Factory(chatRepositoryProvider);
  }

  public static SendChatMessageUseCase newInstance(ChatRepository chatRepository) {
    return new SendChatMessageUseCase(chatRepository);
  }
}
