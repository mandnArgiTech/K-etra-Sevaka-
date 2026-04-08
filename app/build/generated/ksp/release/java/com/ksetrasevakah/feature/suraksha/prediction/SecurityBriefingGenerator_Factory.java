package com.ksetrasevakah.feature.suraksha.prediction;

import com.ksetrasevakah.core.ai.MlcLlmEngine;
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository;
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
public final class SecurityBriefingGenerator_Factory implements Factory<SecurityBriefingGenerator> {
  private final Provider<MlcLlmEngine> engineProvider;

  private final Provider<SecurityEventRepository> securityEventRepositoryProvider;

  public SecurityBriefingGenerator_Factory(Provider<MlcLlmEngine> engineProvider,
      Provider<SecurityEventRepository> securityEventRepositoryProvider) {
    this.engineProvider = engineProvider;
    this.securityEventRepositoryProvider = securityEventRepositoryProvider;
  }

  @Override
  public SecurityBriefingGenerator get() {
    return newInstance(engineProvider.get(), securityEventRepositoryProvider.get());
  }

  public static SecurityBriefingGenerator_Factory create(Provider<MlcLlmEngine> engineProvider,
      Provider<SecurityEventRepository> securityEventRepositoryProvider) {
    return new SecurityBriefingGenerator_Factory(engineProvider, securityEventRepositoryProvider);
  }

  public static SecurityBriefingGenerator newInstance(MlcLlmEngine engine,
      SecurityEventRepository securityEventRepository) {
    return new SecurityBriefingGenerator(engine, securityEventRepository);
  }
}
