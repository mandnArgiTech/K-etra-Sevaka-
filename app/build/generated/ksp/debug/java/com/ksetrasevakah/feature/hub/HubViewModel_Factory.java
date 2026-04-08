package com.ksetrasevakah.feature.hub;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class HubViewModel_Factory implements Factory<HubViewModel> {
  @Override
  public HubViewModel get() {
    return newInstance();
  }

  public static HubViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static HubViewModel newInstance() {
    return new HubViewModel();
  }

  private static final class InstanceHolder {
    private static final HubViewModel_Factory INSTANCE = new HubViewModel_Factory();
  }
}
