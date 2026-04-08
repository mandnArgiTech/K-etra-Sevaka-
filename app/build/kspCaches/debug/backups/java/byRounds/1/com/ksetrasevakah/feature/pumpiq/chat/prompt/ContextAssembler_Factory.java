package com.ksetrasevakah.feature.pumpiq.chat.prompt;

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
public final class ContextAssembler_Factory implements Factory<ContextAssembler> {
  private final Provider<ChatRepository> chatRepositoryProvider;

  public ContextAssembler_Factory(Provider<ChatRepository> chatRepositoryProvider) {
    this.chatRepositoryProvider = chatRepositoryProvider;
  }

  @Override
  public ContextAssembler get() {
    return newInstance(chatRepositoryProvider.get());
  }

  public static ContextAssembler_Factory create(Provider<ChatRepository> chatRepositoryProvider) {
    return new ContextAssembler_Factory(chatRepositoryProvider);
  }

  public static ContextAssembler newInstance(ChatRepository chatRepository) {
    return new ContextAssembler(chatRepository);
  }
}
