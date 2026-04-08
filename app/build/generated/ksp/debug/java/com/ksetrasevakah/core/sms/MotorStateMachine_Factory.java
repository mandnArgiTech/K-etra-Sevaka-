package com.ksetrasevakah.core.sms;

import com.ksetrasevakah.core.database.dao.MotorStateDao;
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
public final class MotorStateMachine_Factory implements Factory<MotorStateMachine> {
  private final Provider<MotorStateDao> motorStateDaoProvider;

  private final Provider<WorkerActivityDao> workerActivityDaoProvider;

  public MotorStateMachine_Factory(Provider<MotorStateDao> motorStateDaoProvider,
      Provider<WorkerActivityDao> workerActivityDaoProvider) {
    this.motorStateDaoProvider = motorStateDaoProvider;
    this.workerActivityDaoProvider = workerActivityDaoProvider;
  }

  @Override
  public MotorStateMachine get() {
    return newInstance(motorStateDaoProvider.get(), workerActivityDaoProvider.get());
  }

  public static MotorStateMachine_Factory create(Provider<MotorStateDao> motorStateDaoProvider,
      Provider<WorkerActivityDao> workerActivityDaoProvider) {
    return new MotorStateMachine_Factory(motorStateDaoProvider, workerActivityDaoProvider);
  }

  public static MotorStateMachine newInstance(MotorStateDao motorStateDao,
      WorkerActivityDao workerActivityDao) {
    return new MotorStateMachine(motorStateDao, workerActivityDao);
  }
}
