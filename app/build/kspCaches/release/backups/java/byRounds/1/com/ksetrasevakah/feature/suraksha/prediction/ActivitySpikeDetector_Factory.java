package com.ksetrasevakah.feature.suraksha.prediction;

import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class ActivitySpikeDetector_Factory implements Factory<ActivitySpikeDetector> {
  private final Provider<SecurityEventRepository> repositoryProvider;

  public ActivitySpikeDetector_Factory(Provider<SecurityEventRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ActivitySpikeDetector get() {
    return newInstance(repositoryProvider.get());
  }

  public static ActivitySpikeDetector_Factory create(
      Provider<SecurityEventRepository> repositoryProvider) {
    return new ActivitySpikeDetector_Factory(repositoryProvider);
  }

  public static ActivitySpikeDetector newInstance(SecurityEventRepository repository) {
    return new ActivitySpikeDetector(repository);
  }
}
