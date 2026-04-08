package com.ksetrasevakah.core.data.repository;

import com.ksetrasevakah.core.database.dao.PredictionCacheDao;
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
public final class PredictionRepositoryImpl_Factory implements Factory<PredictionRepositoryImpl> {
  private final Provider<PredictionCacheDao> daoProvider;

  public PredictionRepositoryImpl_Factory(Provider<PredictionCacheDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public PredictionRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static PredictionRepositoryImpl_Factory create(Provider<PredictionCacheDao> daoProvider) {
    return new PredictionRepositoryImpl_Factory(daoProvider);
  }

  public static PredictionRepositoryImpl newInstance(PredictionCacheDao dao) {
    return new PredictionRepositoryImpl(dao);
  }
}
