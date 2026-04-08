package com.ksetrasevakah.feature.suraksha.data.repository;

import com.ksetrasevakah.core.database.dao.SecurityEventDao;
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
public final class SecurityEventRepositoryImpl_Factory implements Factory<SecurityEventRepositoryImpl> {
  private final Provider<SecurityEventDao> daoProvider;

  public SecurityEventRepositoryImpl_Factory(Provider<SecurityEventDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public SecurityEventRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static SecurityEventRepositoryImpl_Factory create(Provider<SecurityEventDao> daoProvider) {
    return new SecurityEventRepositoryImpl_Factory(daoProvider);
  }

  public static SecurityEventRepositoryImpl newInstance(SecurityEventDao dao) {
    return new SecurityEventRepositoryImpl(dao);
  }
}
