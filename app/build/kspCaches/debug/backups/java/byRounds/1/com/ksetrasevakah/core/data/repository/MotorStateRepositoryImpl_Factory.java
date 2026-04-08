package com.ksetrasevakah.core.data.repository;

import com.ksetrasevakah.core.database.dao.MotorStateDao;
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
public final class MotorStateRepositoryImpl_Factory implements Factory<MotorStateRepositoryImpl> {
  private final Provider<MotorStateDao> daoProvider;

  public MotorStateRepositoryImpl_Factory(Provider<MotorStateDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public MotorStateRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static MotorStateRepositoryImpl_Factory create(Provider<MotorStateDao> daoProvider) {
    return new MotorStateRepositoryImpl_Factory(daoProvider);
  }

  public static MotorStateRepositoryImpl newInstance(MotorStateDao dao) {
    return new MotorStateRepositoryImpl(dao);
  }
}
