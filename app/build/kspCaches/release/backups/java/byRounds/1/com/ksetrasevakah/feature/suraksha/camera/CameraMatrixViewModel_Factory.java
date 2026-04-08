package com.ksetrasevakah.feature.suraksha.camera;

import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository;
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
public final class CameraMatrixViewModel_Factory implements Factory<CameraMatrixViewModel> {
  private final Provider<CameraConfigRepository> cameraConfigRepositoryProvider;

  public CameraMatrixViewModel_Factory(
      Provider<CameraConfigRepository> cameraConfigRepositoryProvider) {
    this.cameraConfigRepositoryProvider = cameraConfigRepositoryProvider;
  }

  @Override
  public CameraMatrixViewModel get() {
    return newInstance(cameraConfigRepositoryProvider.get());
  }

  public static CameraMatrixViewModel_Factory create(
      Provider<CameraConfigRepository> cameraConfigRepositoryProvider) {
    return new CameraMatrixViewModel_Factory(cameraConfigRepositoryProvider);
  }

  public static CameraMatrixViewModel newInstance(CameraConfigRepository cameraConfigRepository) {
    return new CameraMatrixViewModel(cameraConfigRepository);
  }
}
