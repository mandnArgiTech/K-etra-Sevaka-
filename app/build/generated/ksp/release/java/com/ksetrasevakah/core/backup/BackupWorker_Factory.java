package com.ksetrasevakah.core.backup;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
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
public final class BackupWorker_Factory {
  private final Provider<BackupManager> backupManagerProvider;

  public BackupWorker_Factory(Provider<BackupManager> backupManagerProvider) {
    this.backupManagerProvider = backupManagerProvider;
  }

  public BackupWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, backupManagerProvider.get());
  }

  public static BackupWorker_Factory create(Provider<BackupManager> backupManagerProvider) {
    return new BackupWorker_Factory(backupManagerProvider);
  }

  public static BackupWorker newInstance(Context appContext, WorkerParameters workerParams,
      BackupManager backupManager) {
    return new BackupWorker(appContext, workerParams, backupManager);
  }
}
