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
public final class GetChatThreadsUseCase_Factory implements Factory<GetChatThreadsUseCase> {
  private final Provider<ChatRepository> chatRepositoryProvider;

  public GetChatThreadsUseCase_Factory(Provider<ChatRepository> chatRepositoryProvider) {
    this.chatRepositoryProvider = chatRepositoryProvider;
  }

  @Override
  public GetChatThreadsUseCase get() {
    return newInstance(chatRepositoryProvider.get());
  }

  public static GetChatThreadsUseCase_Factory create(
      Provider<ChatRepository> chatRepositoryProvider) {
    return new GetChatThreadsUseCase_Factory(chatRepositoryProvider);
  }

  public static GetChatThreadsUseCase newInstance(ChatRepository chatRepository) {
    return new GetChatThreadsUseCase(chatRepository);
  }
}
