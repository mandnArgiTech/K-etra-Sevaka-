package com.ksetrasevakah.core.vectorstore;

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
public final class RagPipeline_Factory implements Factory<RagPipeline> {
  private final Provider<EmbeddingGenerator> embeddingGeneratorProvider;

  private final Provider<VectorStoreManager> vectorStoreManagerProvider;

  public RagPipeline_Factory(Provider<EmbeddingGenerator> embeddingGeneratorProvider,
      Provider<VectorStoreManager> vectorStoreManagerProvider) {
    this.embeddingGeneratorProvider = embeddingGeneratorProvider;
    this.vectorStoreManagerProvider = vectorStoreManagerProvider;
  }

  @Override
  public RagPipeline get() {
    return newInstance(embeddingGeneratorProvider.get(), vectorStoreManagerProvider.get());
  }

  public static RagPipeline_Factory create(Provider<EmbeddingGenerator> embeddingGeneratorProvider,
      Provider<VectorStoreManager> vectorStoreManagerProvider) {
    return new RagPipeline_Factory(embeddingGeneratorProvider, vectorStoreManagerProvider);
  }

  public static RagPipeline newInstance(EmbeddingGenerator embeddingGenerator,
      VectorStoreManager vectorStoreManager) {
    return new RagPipeline(embeddingGenerator, vectorStoreManager);
  }
}
