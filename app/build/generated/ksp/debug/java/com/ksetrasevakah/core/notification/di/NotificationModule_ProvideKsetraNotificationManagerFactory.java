package com.ksetrasevakah.core.notification.di;

import android.content.Context;
import com.ksetrasevakah.core.notification.KsetraNotificationManager;
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
public final class NotificationModule_ProvideKsetraNotificationManagerFactory implements Factory<KsetraNotificationManager> {
  private final Provider<Context> contextProvider;

  public NotificationModule_ProvideKsetraNotificationManagerFactory(
      Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public KsetraNotificationManager get() {
    return provideKsetraNotificationManager(contextProvider.get());
  }

  public static NotificationModule_ProvideKsetraNotificationManagerFactory create(
      Provider<Context> contextProvider) {
    return new NotificationModule_ProvideKsetraNotificationManagerFactory(contextProvider);
  }

  public static KsetraNotificationManager provideKsetraNotificationManager(Context context) {
    return Preconditions.checkNotNullFromProvides(NotificationModule.INSTANCE.provideKsetraNotificationManager(context));
  }
}
