package com.ksetrasevakah.core.database.di;

import com.ksetrasevakah.core.database.KsetraDatabase;
import com.ksetrasevakah.core.database.dao.PredictionCacheDao;
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
public final class DatabaseModule_ProvidePredictionCacheDaoFactory implements Factory<PredictionCacheDao> {
  private final Provider<KsetraDatabase> dbProvider;

  public DatabaseModule_ProvidePredictionCacheDaoFactory(Provider<KsetraDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PredictionCacheDao get() {
    return providePredictionCacheDao(dbProvider.get());
  }

  public static DatabaseModule_ProvidePredictionCacheDaoFactory create(
      Provider<KsetraDatabase> dbProvider) {
    return new DatabaseModule_ProvidePredictionCacheDaoFactory(dbProvider);
  }

  public static PredictionCacheDao providePredictionCacheDao(KsetraDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePredictionCacheDao(db));
  }
}
