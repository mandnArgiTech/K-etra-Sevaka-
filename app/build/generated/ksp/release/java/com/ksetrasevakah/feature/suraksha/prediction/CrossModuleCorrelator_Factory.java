package com.ksetrasevakah.feature.suraksha.prediction;

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
public final class CrossModuleCorrelator_Factory implements Factory<CrossModuleCorrelator> {
  private final Provider<SecurityEventRepository> securityEventRepositoryProvider;

  public CrossModuleCorrelator_Factory(
      Provider<SecurityEventRepository> securityEventRepositoryProvider) {
    this.securityEventRepositoryProvider = securityEventRepositoryProvider;
  }

  @Override
  public CrossModuleCorrelator get() {
    return newInstance(securityEventRepositoryProvider.get());
  }

  public static CrossModuleCorrelator_Factory create(
      Provider<SecurityEventRepository> securityEventRepositoryProvider) {
    return new CrossModuleCorrelator_Factory(securityEventRepositoryProvider);
  }

  public static CrossModuleCorrelator newInstance(SecurityEventRepository securityEventRepository) {
    return new CrossModuleCorrelator(securityEventRepository);
  }
}
