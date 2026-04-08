package com.ksetrasevakah.core.backup.di;

import com.ksetrasevakah.core.backup.DriveApiClient;
import com.ksetrasevakah.core.backup.RestoreManager;
import com.ksetrasevakah.core.database.KsetraDatabase;
import com.ksetrasevakah.core.database.dao.BackupLogDao;
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
public final class BackupModule_ProvideRestoreManagerFactory implements Factory<RestoreManager> {
  private final Provider<KsetraDatabase> databaseProvider;

  private final Provider<BackupLogDao> backupLogDaoProvider;

  private final Provider<DriveApiClient> driveApiClientProvider;

  public BackupModule_ProvideRestoreManagerFactory(Provider<KsetraDatabase> databaseProvider,
      Provider<BackupLogDao> backupLogDaoProvider,
      Provider<DriveApiClient> driveApiClientProvider) {
    this.databaseProvider = databaseProvider;
    this.backupLogDaoProvider = backupLogDaoProvider;
    this.driveApiClientProvider = driveApiClientProvider;
  }

  @Override
  public RestoreManager get() {
    return provideRestoreManager(databaseProvider.get(), backupLogDaoProvider.get(), driveApiClientProvider.get());
  }

  public static BackupModule_ProvideRestoreManagerFactory create(
      Provider<KsetraDatabase> databaseProvider, Provider<BackupLogDao> backupLogDaoProvider,
      Provider<DriveApiClient> driveApiClientProvider) {
    return new BackupModule_ProvideRestoreManagerFactory(databaseProvider, backupLogDaoProvider, driveApiClientProvider);
  }

  public static RestoreManager provideRestoreManager(KsetraDatabase database,
      BackupLogDao backupLogDao, DriveApiClient driveApiClient) {
    return Preconditions.checkNotNullFromProvides(BackupModule.INSTANCE.provideRestoreManager(database, backupLogDao, driveApiClient));
  }
}
