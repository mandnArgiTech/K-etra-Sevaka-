package com.ksetrasevakah.core.ai.di;

import com.ksetrasevakah.core.ai.MlcLlmEngine;
import com.ksetrasevakah.core.ai.ModelManager;
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
public final class AiModule_Companion_ProvideModelManagerFactory implements Factory<ModelManager> {
  private final Provider<MlcLlmEngine> engineProvider;

  public AiModule_Companion_ProvideModelManagerFactory(Provider<MlcLlmEngine> engineProvider) {
    this.engineProvider = engineProvider;
  }

  @Override
  public ModelManager get() {
    return provideModelManager(engineProvider.get());
  }

  public static AiModule_Companion_ProvideModelManagerFactory create(
      Provider<MlcLlmEngine> engineProvider) {
    return new AiModule_Companion_ProvideModelManagerFactory(engineProvider);
  }

  public static ModelManager provideModelManager(MlcLlmEngine engine) {
    return Preconditions.checkNotNullFromProvides(AiModule.Companion.provideModelManager(engine));
  }
}
