package com.ksetrasevakah.core.data.repository;

import com.ksetrasevakah.core.database.dao.WorkerActivityDao;
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
public final class WorkerActivityRepositoryImpl_Factory implements Factory<WorkerActivityRepositoryImpl> {
  private final Provider<WorkerActivityDao> daoProvider;

  public WorkerActivityRepositoryImpl_Factory(Provider<WorkerActivityDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public WorkerActivityRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static WorkerActivityRepositoryImpl_Factory create(
      Provider<WorkerActivityDao> daoProvider) {
    return new WorkerActivityRepositoryImpl_Factory(daoProvider);
  }

  public static WorkerActivityRepositoryImpl newInstance(WorkerActivityDao dao) {
    return new WorkerActivityRepositoryImpl(dao);
  }
}
