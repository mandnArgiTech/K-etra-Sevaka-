package com.ksetrasevakah.core.sms.di;

import android.content.Context;
import android.telephony.SmsManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SmsModule_Companion_ProvideSmsManagerFactory implements Factory<SmsManager> {
  private final Provider<Context> contextProvider;

  public SmsModule_Companion_ProvideSmsManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SmsManager get() {
    return provideSmsManager(contextProvider.get());
  }

  public static SmsModule_Companion_ProvideSmsManagerFactory create(
      Provider<Context> contextProvider) {
    return new SmsModule_Companion_ProvideSmsManagerFactory(contextProvider);
  }

  public static SmsManager provideSmsManager(Context context) {
    return Preconditions.checkNotNullFromProvides(SmsModule.Companion.provideSmsManager(context));
  }
}
