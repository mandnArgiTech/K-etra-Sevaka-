package com.ksetrasevakah.feature.pumpiq.domain.usecase;

import com.ksetrasevakah.core.database.dao.FaultDao;
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
public final class ProcessSmsConfirmationUseCase_Factory implements Factory<ProcessSmsConfirmationUseCase> {
  private final Provider<MotorStateDao> motorStateDaoProvider;

  private final Provider<FaultDao> faultDaoProvider;

  public ProcessSmsConfirmationUseCase_Factory(Provider<MotorStateDao> motorStateDaoProvider,
      Provider<FaultDao> faultDaoProvider) {
    this.motorStateDaoProvider = motorStateDaoProvider;
    this.faultDaoProvider = faultDaoProvider;
  }

  @Override
  public ProcessSmsConfirmationUseCase get() {
    return newInstance(motorStateDaoProvider.get(), faultDaoProvider.get());
  }

  public static ProcessSmsConfirmationUseCase_Factory create(
      Provider<MotorStateDao> motorStateDaoProvider, Provider<FaultDao> faultDaoProvider) {
    return new ProcessSmsConfirmationUseCase_Factory(motorStateDaoProvider, faultDaoProvider);
  }

  public static ProcessSmsConfirmationUseCase newInstance(MotorStateDao motorStateDao,
      FaultDao faultDao) {
    return new ProcessSmsConfirmationUseCase(motorStateDao, faultDao);
  }
}
