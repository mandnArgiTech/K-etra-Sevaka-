package com.ksetrasevakah.feature.suraksha.data.repository;

import com.ksetrasevakah.core.database.dao.CameraConfigDao;
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
public final class CameraConfigRepositoryImpl_Factory implements Factory<CameraConfigRepositoryImpl> {
  private final Provider<CameraConfigDao> daoProvider;

  public CameraConfigRepositoryImpl_Factory(Provider<CameraConfigDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public CameraConfigRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static CameraConfigRepositoryImpl_Factory create(Provider<CameraConfigDao> daoProvider) {
    return new CameraConfigRepositoryImpl_Factory(daoProvider);
  }

  public static CameraConfigRepositoryImpl newInstance(CameraConfigDao dao) {
    return new CameraConfigRepositoryImpl(dao);
  }
}
