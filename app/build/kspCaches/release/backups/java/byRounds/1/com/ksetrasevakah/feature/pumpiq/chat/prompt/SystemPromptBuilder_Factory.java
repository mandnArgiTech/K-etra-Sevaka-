package com.ksetrasevakah.feature.pumpiq.chat.prompt;

import com.ksetrasevakah.core.domain.repository.MotorStateRepository;
import com.ksetrasevakah.core.domain.repository.PredictionRepository;
import com.ksetrasevakah.core.vectorstore.RagPipeline;
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
public final class SystemPromptBuilder_Factory implements Factory<SystemPromptBuilder> {
  private final Provider<MotorStateRepository> motorStateRepositoryProvider;

  private final Provider<PredictionRepository> predictionRepositoryProvider;

  private final Provider<RagPipeline> ragPipelineProvider;

  public SystemPromptBuilder_Factory(Provider<MotorStateRepository> motorStateRepositoryProvider,
      Provider<PredictionRepository> predictionRepositoryProvider,
      Provider<RagPipeline> ragPipelineProvider) {
    this.motorStateRepositoryProvider = motorStateRepositoryProvider;
    this.predictionRepositoryProvider = predictionRepositoryProvider;
    this.ragPipelineProvider = ragPipelineProvider;
  }

  @Override
  public SystemPromptBuilder get() {
    return newInstance(motorStateRepositoryProvider.get(), predictionRepositoryProvider.get(), ragPipelineProvider.get());
  }

  public static SystemPromptBuilder_Factory create(
      Provider<MotorStateRepository> motorStateRepositoryProvider,
      Provider<PredictionRepository> predictionRepositoryProvider,
      Provider<RagPipeline> ragPipelineProvider) {
    return new SystemPromptBuilder_Factory(motorStateRepositoryProvider, predictionRepositoryProvider, ragPipelineProvider);
  }

  public static SystemPromptBuilder newInstance(MotorStateRepository motorStateRepository,
      PredictionRepository predictionRepository, RagPipeline ragPipeline) {
    return new SystemPromptBuilder(motorStateRepository, predictionRepository, ragPipeline);
  }
}
