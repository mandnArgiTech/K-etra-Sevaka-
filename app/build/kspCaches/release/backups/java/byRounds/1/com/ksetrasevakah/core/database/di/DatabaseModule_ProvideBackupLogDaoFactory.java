package com.ksetrasevakah.core.database.di;

import com.ksetrasevakah.core.database.KsetraDatabase;
import com.ksetrasevakah.core.database.dao.BackupLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideBackupLogDaoFactory implements Factory<BackupLogDao> {
  private final Provider<KsetraDatabase> dbProvider;

  public DatabaseModule_ProvideBackupLogDaoFactory(Provider<KsetraDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BackupLogDao get() {
    return provideBackupLogDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideBackupLogDaoFactory create(
      Provider<KsetraDatabase> dbProvider) {
    return new DatabaseModule_ProvideBackupLogDaoFactory(dbProvider);
  }

  public static BackupLogDao provideBackupLogDao(KsetraDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBackupLogDao(db));
  }
}
