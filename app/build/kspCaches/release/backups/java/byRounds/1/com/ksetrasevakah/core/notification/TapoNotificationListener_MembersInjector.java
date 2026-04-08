package com.ksetrasevakah.core.notification;

import com.ksetrasevakah.feature.suraksha.prediction.ThreatRouter;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class TapoNotificationListener_MembersInjector implements MembersInjector<TapoNotificationListener> {
  private final Provider<ThreatRouter> threatRouterProvider;

  public TapoNotificationListener_MembersInjector(Provider<ThreatRouter> threatRouterProvider) {
    this.threatRouterProvider = threatRouterProvider;
  }

  public static MembersInjector<TapoNotificationListener> create(
      Provider<ThreatRouter> threatRouterProvider) {
    return new TapoNotificationListener_MembersInjector(threatRouterProvider);
  }

  @Override
  public void injectMembers(TapoNotificationListener instance) {
    injectThreatRouter(instance, threatRouterProvider.get());
  }

  @InjectedFieldSignature("com.ksetrasevakah.core.notification.TapoNotificationListener.threatRouter")
  public static void injectThreatRouter(TapoNotificationListener instance,
      ThreatRouter threatRouter) {
    instance.threatRouter = threatRouter;
  }
}
