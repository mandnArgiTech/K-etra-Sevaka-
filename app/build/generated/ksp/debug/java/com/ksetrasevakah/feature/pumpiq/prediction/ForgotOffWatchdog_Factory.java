package com.ksetrasevakah.feature.pumpiq.prediction;

import com.ksetrasevakah.core.domain.repository.MotorStateRepository;
import com.ksetrasevakah.core.domain.repository.WorkerActivityRepository;
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
public final class ForgotOffWatchdog_Factory implements Factory<ForgotOffWatchdog> {
  private final Provider<MotorStateRepository> motorStateRepositoryProvider;

  private final Provider<WorkerActivityRepository> workerActivityRepositoryProvider;

  public ForgotOffWatchdog_Factory(Provider<MotorStateRepository> motorStateRepositoryProvider,
      Provider<WorkerActivityRepository> workerActivityRepositoryProvider) {
    this.motorStateRepositoryProvider = motorStateRepositoryProvider;
    this.workerActivityRepositoryProvider = workerActivityRepositoryProvider;
  }

  @Override
  public ForgotOffWatchdog get() {
    return newInstance(motorStateRepositoryProvider.get(), workerActivityRepositoryProvider.get());
  }

  public static ForgotOffWatchdog_Factory create(
      Provider<MotorStateRepository> motorStateRepositoryProvider,
      Provider<WorkerActivityRepository> workerActivityRepositoryProvider) {
    return new ForgotOffWatchdog_Factory(motorStateRepositoryProvider, workerActivityRepositoryProvider);
  }

  public static ForgotOffWatchdog newInstance(MotorStateRepository motorStateRepository,
      WorkerActivityRepository workerActivityRepository) {
    return new ForgotOffWatchdog(motorStateRepository, workerActivityRepository);
  }
}
