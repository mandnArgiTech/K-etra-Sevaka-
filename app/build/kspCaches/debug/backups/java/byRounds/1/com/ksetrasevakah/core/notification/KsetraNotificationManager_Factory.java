package com.ksetrasevakah.core.notification;

import android.content.Context;
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
public final class KsetraNotificationManager_Factory implements Factory<KsetraNotificationManager> {
  private final Provider<Context> contextProvider;

  public KsetraNotificationManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public KsetraNotificationManager get() {
    return newInstance(contextProvider.get());
  }

  public static KsetraNotificationManager_Factory create(Provider<Context> contextProvider) {
    return new KsetraNotificationManager_Factory(contextProvider);
  }

  public static KsetraNotificationManager newInstance(Context context) {
    return new KsetraNotificationManager(context);
  }
}
