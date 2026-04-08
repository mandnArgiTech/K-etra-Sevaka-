package com.ksetrasevakah.feature.pumpiq.dashboard;

import com.ksetrasevakah.core.domain.repository.MotorStateRepository;
import com.ksetrasevakah.core.domain.repository.TelemetryRepository;
import com.ksetrasevakah.feature.pumpiq.domain.usecase.SendSmsCommandUseCase;
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<MotorStateRepository> motorStateRepositoryProvider;

  private final Provider<TelemetryRepository> telemetryRepositoryProvider;

  private final Provider<SendSmsCommandUseCase> sendSmsCommandUseCaseProvider;

  public DashboardViewModel_Factory(Provider<MotorStateRepository> motorStateRepositoryProvider,
      Provider<TelemetryRepository> telemetryRepositoryProvider,
      Provider<SendSmsCommandUseCase> sendSmsCommandUseCaseProvider) {
    this.motorStateRepositoryProvider = motorStateRepositoryProvider;
    this.telemetryRepositoryProvider = telemetryRepositoryProvider;
    this.sendSmsCommandUseCaseProvider = sendSmsCommandUseCaseProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(motorStateRepositoryProvider.get(), telemetryRepositoryProvider.get(), sendSmsCommandUseCaseProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<MotorStateRepository> motorStateRepositoryProvider,
      Provider<TelemetryRepository> telemetryRepositoryProvider,
      Provider<SendSmsCommandUseCase> sendSmsCommandUseCaseProvider) {
    return new DashboardViewModel_Factory(motorStateRepositoryProvider, telemetryRepositoryProvider, sendSmsCommandUseCaseProvider);
  }

  public static DashboardViewModel newInstance(MotorStateRepository motorStateRepository,
      TelemetryRepository telemetryRepository, SendSmsCommandUseCase sendSmsCommandUseCase) {
    return new DashboardViewModel(motorStateRepository, telemetryRepository, sendSmsCommandUseCase);
  }
}
