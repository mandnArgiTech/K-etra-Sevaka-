package com.ksetrasevakah.core.data.repository;

import com.ksetrasevakah.core.database.dao.FaultDao;
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
public final class FaultRepositoryImpl_Factory implements Factory<FaultRepositoryImpl> {
  private final Provider<FaultDao> daoProvider;

  public FaultRepositoryImpl_Factory(Provider<FaultDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public FaultRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static FaultRepositoryImpl_Factory create(Provider<FaultDao> daoProvider) {
    return new FaultRepositoryImpl_Factory(daoProvider);
  }

  public static FaultRepositoryImpl newInstance(FaultDao dao) {
    return new FaultRepositoryImpl(dao);
  }
}
