package com.ksetrasevakah.feature.pumpiq.chat;

import com.ksetrasevakah.core.ai.MlcLlmEngine;
import com.ksetrasevakah.core.ai.ModelManager;
import com.ksetrasevakah.core.domain.repository.ChatRepository;
import com.ksetrasevakah.feature.pumpiq.chat.prompt.ContextAssembler;
import com.ksetrasevakah.feature.pumpiq.chat.prompt.SystemPromptBuilder;
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
public final class ChatOrchestrator_Factory implements Factory<ChatOrchestrator> {
  private final Provider<MlcLlmEngine> engineProvider;

  private final Provider<ModelManager> modelManagerProvider;

  private final Provider<ChatRepository> chatRepositoryProvider;

  private final Provider<SystemPromptBuilder> systemPromptBuilderProvider;

  private final Provider<ContextAssembler> contextAssemblerProvider;

  public ChatOrchestrator_Factory(Provider<MlcLlmEngine> engineProvider,
      Provider<ModelManager> modelManagerProvider, Provider<ChatRepository> chatRepositoryProvider,
      Provider<SystemPromptBuilder> systemPromptBuilderProvider,
      Provider<ContextAssembler> contextAssemblerProvider) {
    this.engineProvider = engineProvider;
    this.modelManagerProvider = modelManagerProvider;
    this.chatRepositoryProvider = chatRepositoryProvider;
    this.systemPromptBuilderProvider = systemPromptBuilderProvider;
    this.contextAssemblerProvider = contextAssemblerProvider;
  }

  @Override
  public ChatOrchestrator get() {
    return newInstance(engineProvider.get(), modelManagerProvider.get(), chatRepositoryProvider.get(), systemPromptBuilderProvider.get(), contextAssemblerProvider.get());
  }

  public static ChatOrchestrator_Factory create(Provider<MlcLlmEngine> engineProvider,
      Provider<ModelManager> modelManagerProvider, Provider<ChatRepository> chatRepositoryProvider,
      Provider<SystemPromptBuilder> systemPromptBuilderProvider,
      Provider<ContextAssembler> contextAssemblerProvider) {
    return new ChatOrchestrator_Factory(engineProvider, modelManagerProvider, chatRepositoryProvider, systemPromptBuilderProvider, contextAssemblerProvider);
  }

  public static ChatOrchestrator newInstance(MlcLlmEngine engine, ModelManager modelManager,
      ChatRepository chatRepository, SystemPromptBuilder systemPromptBuilder,
      ContextAssembler contextAssembler) {
    return new ChatOrchestrator(engine, modelManager, chatRepository, systemPromptBuilder, contextAssembler);
  }
}
