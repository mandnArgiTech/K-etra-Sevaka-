package com.ksetrasevakah.core.sms;

import android.content.Context;
import android.telephony.SmsManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DefaultSmsCommandSender_Factory implements Factory<DefaultSmsCommandSender> {
  private final Provider<Context> contextProvider;

  private final Provider<SmsManager> smsManagerProvider;

  public DefaultSmsCommandSender_Factory(Provider<Context> contextProvider,
      Provider<SmsManager> smsManagerProvider) {
    this.contextProvider = contextProvider;
    this.smsManagerProvider = smsManagerProvider;
  }

  @Override
  public DefaultSmsCommandSender get() {
    return newInstance(contextProvider.get(), smsManagerProvider.get());
  }

  public static DefaultSmsCommandSender_Factory create(Provider<Context> contextProvider,
      Provider<SmsManager> smsManagerProvider) {
    return new DefaultSmsCommandSender_Factory(contextProvider, smsManagerProvider);
  }

  public static DefaultSmsCommandSender newInstance(Context context, SmsManager smsManager) {
    return new DefaultSmsCommandSender(context, smsManager);
  }
}
