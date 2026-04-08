package com.ksetrasevakah.core.vectorstore.di;

import com.ksetrasevakah.core.vectorstore.EmbeddingGenerator;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class VectorStoreModule_ProvideEmbeddingGeneratorFactory implements Factory<EmbeddingGenerator> {
  @Override
  public EmbeddingGenerator get() {
    return provideEmbeddingGenerator();
  }

  public static VectorStoreModule_ProvideEmbeddingGeneratorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static EmbeddingGenerator provideEmbeddingGenerator() {
    return Preconditions.checkNotNullFromProvides(VectorStoreModule.INSTANCE.provideEmbeddingGenerator());
  }

  private static final class InstanceHolder {
    private static final VectorStoreModule_ProvideEmbeddingGeneratorFactory INSTANCE = new VectorStoreModule_ProvideEmbeddingGeneratorFactory();
  }
}
