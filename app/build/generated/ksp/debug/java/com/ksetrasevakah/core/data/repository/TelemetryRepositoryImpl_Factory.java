package com.ksetrasevakah.core.data.repository;

import com.ksetrasevakah.core.database.dao.TelemetryDao;
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
public final class TelemetryRepositoryImpl_Factory implements Factory<TelemetryRepositoryImpl> {
  private final Provider<TelemetryDao> daoProvider;

  public TelemetryRepositoryImpl_Factory(Provider<TelemetryDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public TelemetryRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static TelemetryRepositoryImpl_Factory create(Provider<TelemetryDao> daoProvider) {
    return new TelemetryRepositoryImpl_Factory(daoProvider);
  }

  public static TelemetryRepositoryImpl newInstance(TelemetryDao dao) {
    return new TelemetryRepositoryImpl(dao);
  }
}
