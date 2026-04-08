package com.ksetrasevakah.feature.suraksha.dashboard;

import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository;
import com.ksetrasevakah.feature.suraksha.prediction.SecurityBriefingGenerator;
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
public final class SurakshaDashboardViewModel_Factory implements Factory<SurakshaDashboardViewModel> {
  private final Provider<SecurityEventRepository> securityEventRepositoryProvider;

  private final Provider<SecurityBriefingGenerator> briefingGeneratorProvider;

  public SurakshaDashboardViewModel_Factory(
      Provider<SecurityEventRepository> securityEventRepositoryProvider,
      Provider<SecurityBriefingGenerator> briefingGeneratorProvider) {
    this.securityEventRepositoryProvider = securityEventRepositoryProvider;
    this.briefingGeneratorProvider = briefingGeneratorProvider;
  }

  @Override
  public SurakshaDashboardViewModel get() {
    return newInstance(securityEventRepositoryProvider.get(), briefingGeneratorProvider.get());
  }

  public static SurakshaDashboardViewModel_Factory create(
      Provider<SecurityEventRepository> securityEventRepositoryProvider,
      Provider<SecurityBriefingGenerator> briefingGeneratorProvider) {
    return new SurakshaDashboardViewModel_Factory(securityEventRepositoryProvider, briefingGeneratorProvider);
  }

  public static SurakshaDashboardViewModel newInstance(
      SecurityEventRepository securityEventRepository,
      SecurityBriefingGenerator briefingGenerator) {
    return new SurakshaDashboardViewModel(securityEventRepository, briefingGenerator);
  }
}
