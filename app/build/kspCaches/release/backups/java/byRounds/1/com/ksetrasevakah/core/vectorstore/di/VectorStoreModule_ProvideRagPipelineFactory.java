package com.ksetrasevakah.core.vectorstore.di;

import com.ksetrasevakah.core.vectorstore.EmbeddingGenerator;
import com.ksetrasevakah.core.vectorstore.RagPipeline;
import com.ksetrasevakah.core.vectorstore.VectorStoreManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class VectorStoreModule_ProvideRagPipelineFactory implements Factory<RagPipeline> {
  private final Provider<EmbeddingGenerator> embeddingGeneratorProvider;

  private final Provider<VectorStoreManager> vectorStoreManagerProvider;

  public VectorStoreModule_ProvideRagPipelineFactory(
      Provider<EmbeddingGenerator> embeddingGeneratorProvider,
      Provider<VectorStoreManager> vectorStoreManagerProvider) {
    this.embeddingGeneratorProvider = embeddingGeneratorProvider;
    this.vectorStoreManagerProvider = vectorStoreManagerProvider;
  }

  @Override
  public RagPipeline get() {
    return provideRagPipeline(embeddingGeneratorProvider.get(), vectorStoreManagerProvider.get());
  }

  public static VectorStoreModule_ProvideRagPipelineFactory create(
      Provider<EmbeddingGenerator> embeddingGeneratorProvider,
      Provider<VectorStoreManager> vectorStoreManagerProvider) {
    return new VectorStoreModule_ProvideRagPipelineFactory(embeddingGeneratorProvider, vectorStoreManagerProvider);
  }

  public static RagPipeline provideRagPipeline(EmbeddingGenerator embeddingGenerator,
      VectorStoreManager vectorStoreManager) {
    return Preconditions.checkNotNullFromProvides(VectorStoreModule.INSTANCE.provideRagPipeline(embeddingGenerator, vectorStoreManager));
  }
}
