package com.ksetrasevakah.core.ai;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

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
public final class ModelManager_Factory implements Factory<ModelManager> {
  private final Provider<MlcLlmEngine> engineProvider;

  private final Provider<CoroutineScope> scopeProvider;

  public ModelManager_Factory(Provider<MlcLlmEngine> engineProvider,
      Provider<CoroutineScope> scopeProvider) {
    this.engineProvider = engineProvider;
    this.scopeProvider = scopeProvider;
  }

  @Override
  public ModelManager get() {
    return newInstance(engineProvider.get(), scopeProvider.get());
  }

  public static ModelManager_Factory create(Provider<MlcLlmEngine> engineProvider,
      Provider<CoroutineScope> scopeProvider) {
    return new ModelManager_Factory(engineProvider, scopeProvider);
  }

  public static ModelManager newInstance(MlcLlmEngine engine, CoroutineScope scope) {
    return new ModelManager(engine, scope);
  }
}
