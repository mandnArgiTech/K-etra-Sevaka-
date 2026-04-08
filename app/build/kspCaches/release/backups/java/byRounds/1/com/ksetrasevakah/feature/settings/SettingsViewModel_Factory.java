package com.ksetrasevakah.feature.settings;

import com.ksetrasevakah.core.backup.BackupManager;
import com.ksetrasevakah.core.domain.repository.MotorStateRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<MotorStateRepository> motorStateRepositoryProvider;

  private final Provider<BackupManager> backupManagerProvider;

  public SettingsViewModel_Factory(Provider<MotorStateRepository> motorStateRepositoryProvider,
      Provider<BackupManager> backupManagerProvider) {
    this.motorStateRepositoryProvider = motorStateRepositoryProvider;
    this.backupManagerProvider = backupManagerProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(motorStateRepositoryProvider.get(), backupManagerProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<MotorStateRepository> motorStateRepositoryProvider,
      Provider<BackupManager> backupManagerProvider) {
    return new SettingsViewModel_Factory(motorStateRepositoryProvider, backupManagerProvider);
  }

  public static SettingsViewModel newInstance(MotorStateRepository motorStateRepository,
      BackupManager backupManager) {
    return new SettingsViewModel(motorStateRepository, backupManager);
  }
}
