package com.ksetrasevakah.core.ai;

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
public final class NarrativeGenerator_Factory implements Factory<NarrativeGenerator> {
  private final Provider<MlcLlmEngine> engineProvider;

  public NarrativeGenerator_Factory(Provider<MlcLlmEngine> engineProvider) {
    this.engineProvider = engineProvider;
  }

  @Override
  public NarrativeGenerator get() {
    return newInstance(engineProvider.get());
  }

  public static NarrativeGenerator_Factory create(Provider<MlcLlmEngine> engineProvider) {
    return new NarrativeGenerator_Factory(engineProvider);
  }

  public static NarrativeGenerator newInstance(MlcLlmEngine engine) {
    return new NarrativeGenerator(engine);
  }
}
