package com.ksetrasevakah.core.backup;

import com.ksetrasevakah.core.database.KsetraDatabase;
import com.ksetrasevakah.core.database.dao.BackupLogDao;
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
public final class RestoreManager_Factory implements Factory<RestoreManager> {
  private final Provider<KsetraDatabase> databaseProvider;

  private final Provider<BackupLogDao> backupLogDaoProvider;

  private final Provider<DriveApiClient> driveApiClientProvider;

  public RestoreManager_Factory(Provider<KsetraDatabase> databaseProvider,
      Provider<BackupLogDao> backupLogDaoProvider,
      Provider<DriveApiClient> driveApiClientProvider) {
    this.databaseProvider = databaseProvider;
    this.backupLogDaoProvider = backupLogDaoProvider;
    this.driveApiClientProvider = driveApiClientProvider;
  }

  @Override
  public RestoreManager get() {
    return newInstance(databaseProvider.get(), backupLogDaoProvider.get(), driveApiClientProvider.get());
  }

  public static RestoreManager_Factory create(Provider<KsetraDatabase> databaseProvider,
      Provider<BackupLogDao> backupLogDaoProvider,
      Provider<DriveApiClient> driveApiClientProvider) {
    return new RestoreManager_Factory(databaseProvider, backupLogDaoProvider, driveApiClientProvider);
  }

  public static RestoreManager newInstance(KsetraDatabase database, BackupLogDao backupLogDao,
      DriveApiClient driveApiClient) {
    return new RestoreManager(database, backupLogDao, driveApiClient);
  }
}
