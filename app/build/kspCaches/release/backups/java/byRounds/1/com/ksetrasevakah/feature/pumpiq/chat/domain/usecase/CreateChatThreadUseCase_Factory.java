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
public final class CreateChatThreadUseCase_Factory implements Factory<CreateChatThreadUseCase> {
  private final Provider<ChatRepository> chatRepositoryProvider;

  public CreateChatThreadUseCase_Factory(Provider<ChatRepository> chatRepositoryProvider) {
    this.chatRepositoryProvider = chatRepositoryProvider;
  }

  @Override
  public CreateChatThreadUseCase get() {
    return newInstance(chatRepositoryProvider.get());
  }

  public static CreateChatThreadUseCase_Factory create(
      Provider<ChatRepository> chatRepositoryProvider) {
    return new CreateChatThreadUseCase_Factory(chatRepositoryProvider);
  }

  public static CreateChatThreadUseCase newInstance(ChatRepository chatRepository) {
    return new CreateChatThreadUseCase(chatRepository);
  }
}
