package com.ksetrasevakah.feature.pumpiq.domain.usecase;

import com.ksetrasevakah.core.database.dao.MotorStateDao;
import com.ksetrasevakah.core.sms.SmsCommandSender;
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
public final class SendSmsCommandUseCase_Factory implements Factory<SendSmsCommandUseCase> {
  private final Provider<SmsCommandSender> senderProvider;

  private final Provider<MotorStateDao> motorStateDaoProvider;

  public SendSmsCommandUseCase_Factory(Provider<SmsCommandSender> senderProvider,
      Provider<MotorStateDao> motorStateDaoProvider) {
    this.senderProvider = senderProvider;
    this.motorStateDaoProvider = motorStateDaoProvider;
  }

  @Override
  public SendSmsCommandUseCase get() {
    return newInstance(senderProvider.get(), motorStateDaoProvider.get());
  }

  public static SendSmsCommandUseCase_Factory create(Provider<SmsCommandSender> senderProvider,
      Provider<MotorStateDao> motorStateDaoProvider) {
    return new SendSmsCommandUseCase_Factory(senderProvider, motorStateDaoProvider);
  }

  public static SendSmsCommandUseCase newInstance(SmsCommandSender sender,
      MotorStateDao motorStateDao) {
    return new SendSmsCommandUseCase(sender, motorStateDao);
  }
}
