package com.ksetrasevakah.core.vectorstore;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class EmbeddingGenerator_Factory implements Factory<EmbeddingGenerator> {
  @Override
  public EmbeddingGenerator get() {
    return newInstance();
  }

  public static EmbeddingGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static EmbeddingGenerator newInstance() {
    return new EmbeddingGenerator();
  }

  private static final class InstanceHolder {
    private static final EmbeddingGenerator_Factory INSTANCE = new EmbeddingGenerator_Factory();
  }
}
