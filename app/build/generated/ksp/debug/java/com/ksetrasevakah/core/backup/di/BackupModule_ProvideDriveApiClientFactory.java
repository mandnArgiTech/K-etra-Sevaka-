package com.ksetrasevakah.core.backup.di;

import com.ksetrasevakah.core.backup.DriveApiClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class BackupModule_ProvideDriveApiClientFactory implements Factory<DriveApiClient> {
  @Override
  public DriveApiClient get() {
    return provideDriveApiClient();
  }

  public static BackupModule_ProvideDriveApiClientFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DriveApiClient provideDriveApiClient() {
    return Preconditions.checkNotNullFromProvides(BackupModule.INSTANCE.provideDriveApiClient());
  }

  private static final class InstanceHolder {
    private static final BackupModule_ProvideDriveApiClientFactory INSTANCE = new BackupModule_ProvideDriveApiClientFactory();
  }
}
