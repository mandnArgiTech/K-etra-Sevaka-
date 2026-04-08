package com.ksetrasevakah.core.backup.di;

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
public final class BackupModule_ProvideBackupLogDaoFactory implements Factory<BackupLogDao> {
  private final Provider<KsetraDatabase> databaseProvider;

  public BackupModule_ProvideBackupLogDaoFactory(Provider<KsetraDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public BackupLogDao get() {
    return provideBackupLogDao(databaseProvider.get());
  }

  public static BackupModule_ProvideBackupLogDaoFactory create(
      Provider<KsetraDatabase> databaseProvider) {
    return new BackupModule_ProvideBackupLogDaoFactory(databaseProvider);
  }

  public static BackupLogDao provideBackupLogDao(KsetraDatabase database) {
    return Preconditions.checkNotNullFromProvides(BackupModule.INSTANCE.provideBackupLogDao(database));
  }
}
