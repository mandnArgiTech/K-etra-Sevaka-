package com.ksetrasevakah.core.notification;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class CriticalAlarmManager_Factory implements Factory<CriticalAlarmManager> {
  private final Provider<Context> contextProvider;

  private final Provider<KsetraNotificationManager> notificationManagerProvider;

  public CriticalAlarmManager_Factory(Provider<Context> contextProvider,
      Provider<KsetraNotificationManager> notificationManagerProvider) {
    this.contextProvider = contextProvider;
    this.notificationManagerProvider = notificationManagerProvider;
  }

  @Override
  public CriticalAlarmManager get() {
    return newInstance(contextProvider.get(), notificationManagerProvider.get());
  }

  public static CriticalAlarmManager_Factory create(Provider<Context> contextProvider,
      Provider<KsetraNotificationManager> notificationManagerProvider) {
    return new CriticalAlarmManager_Factory(contextProvider, notificationManagerProvider);
  }

  public static CriticalAlarmManager newInstance(Context context,
      KsetraNotificationManager notificationManager) {
    return new CriticalAlarmManager(context, notificationManager);
  }
}
