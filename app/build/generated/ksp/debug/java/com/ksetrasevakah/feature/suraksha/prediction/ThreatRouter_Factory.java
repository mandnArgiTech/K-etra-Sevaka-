package com.ksetrasevakah.feature.suraksha.prediction;

import com.ksetrasevakah.core.notification.CriticalAlarmManager;
import com.ksetrasevakah.core.notification.KsetraNotificationManager;
import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository;
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository;
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
public final class ThreatRouter_Factory implements Factory<ThreatRouter> {
  private final Provider<ThreatClassifier> classifierProvider;

  private final Provider<SecurityEventRepository> eventRepositoryProvider;

  private final Provider<CameraConfigRepository> cameraConfigRepositoryProvider;

  private final Provider<KsetraNotificationManager> notificationManagerProvider;

  private final Provider<CriticalAlarmManager> criticalAlarmManagerProvider;

  public ThreatRouter_Factory(Provider<ThreatClassifier> classifierProvider,
      Provider<SecurityEventRepository> eventRepositoryProvider,
      Provider<CameraConfigRepository> cameraConfigRepositoryProvider,
      Provider<KsetraNotificationManager> notificationManagerProvider,
      Provider<CriticalAlarmManager> criticalAlarmManagerProvider) {
    this.classifierProvider = classifierProvider;
    this.eventRepositoryProvider = eventRepositoryProvider;
    this.cameraConfigRepositoryProvider = cameraConfigRepositoryProvider;
    this.notificationManagerProvider = notificationManagerProvider;
    this.criticalAlarmManagerProvider = criticalAlarmManagerProvider;
  }

  @Override
  public ThreatRouter get() {
    return newInstance(classifierProvider.get(), eventRepositoryProvider.get(), cameraConfigRepositoryProvider.get(), notificationManagerProvider.get(), criticalAlarmManagerProvider.get());
  }

  public static ThreatRouter_Factory create(Provider<ThreatClassifier> classifierProvider,
      Provider<SecurityEventRepository> eventRepositoryProvider,
      Provider<CameraConfigRepository> cameraConfigRepositoryProvider,
      Provider<KsetraNotificationManager> notificationManagerProvider,
      Provider<CriticalAlarmManager> criticalAlarmManagerProvider) {
    return new ThreatRouter_Factory(classifierProvider, eventRepositoryProvider, cameraConfigRepositoryProvider, notificationManagerProvider, criticalAlarmManagerProvider);
  }

  public static ThreatRouter newInstance(ThreatClassifier classifier,
      SecurityEventRepository eventRepository, CameraConfigRepository cameraConfigRepository,
      KsetraNotificationManager notificationManager, CriticalAlarmManager criticalAlarmManager) {
    return new ThreatRouter(classifier, eventRepository, cameraConfigRepository, notificationManager, criticalAlarmManager);
  }
}
