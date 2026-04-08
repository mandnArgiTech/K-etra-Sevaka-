package com.ksetrasevakah.core.database.di;

import com.ksetrasevakah.core.database.KsetraDatabase;
import com.ksetrasevakah.core.database.dao.MotorStateDao;
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
public final class DatabaseModule_ProvideMotorStateDaoFactory implements Factory<MotorStateDao> {
  private final Provider<KsetraDatabase> dbProvider;

  public DatabaseModule_ProvideMotorStateDaoFactory(Provider<KsetraDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MotorStateDao get() {
    return provideMotorStateDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideMotorStateDaoFactory create(
      Provider<KsetraDatabase> dbProvider) {
    return new DatabaseModule_ProvideMotorStateDaoFactory(dbProvider);
  }

  public static MotorStateDao provideMotorStateDao(KsetraDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMotorStateDao(db));
  }
}
