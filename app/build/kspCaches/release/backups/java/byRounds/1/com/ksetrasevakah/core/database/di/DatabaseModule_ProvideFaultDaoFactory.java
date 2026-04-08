package com.ksetrasevakah.core.database.di;

import com.ksetrasevakah.core.database.KsetraDatabase;
import com.ksetrasevakah.core.database.dao.FaultDao;
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
public final class DatabaseModule_ProvideFaultDaoFactory implements Factory<FaultDao> {
  private final Provider<KsetraDatabase> dbProvider;

  public DatabaseModule_ProvideFaultDaoFactory(Provider<KsetraDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public FaultDao get() {
    return provideFaultDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideFaultDaoFactory create(Provider<KsetraDatabase> dbProvider) {
    return new DatabaseModule_ProvideFaultDaoFactory(dbProvider);
  }

  public static FaultDao provideFaultDao(KsetraDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFaultDao(db));
  }
}
