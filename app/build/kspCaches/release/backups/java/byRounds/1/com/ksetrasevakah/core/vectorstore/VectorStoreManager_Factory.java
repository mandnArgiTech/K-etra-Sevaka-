package com.ksetrasevakah.core.vectorstore;

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
public final class VectorStoreManager_Factory implements Factory<VectorStoreManager> {
  @Override
  public VectorStoreManager get() {
    return newInstance();
  }

  public static VectorStoreManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VectorStoreManager newInstance() {
    return new VectorStoreManager();
  }

  private static final class InstanceHolder {
    private static final VectorStoreManager_Factory INSTANCE = new VectorStoreManager_Factory();
  }
}
