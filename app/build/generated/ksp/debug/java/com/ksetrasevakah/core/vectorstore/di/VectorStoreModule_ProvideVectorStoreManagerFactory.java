package com.ksetrasevakah.core.vectorstore.di;

import com.ksetrasevakah.core.vectorstore.VectorStoreManager;
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
public final class VectorStoreModule_ProvideVectorStoreManagerFactory implements Factory<VectorStoreManager> {
  @Override
  public VectorStoreManager get() {
    return provideVectorStoreManager();
  }

  public static VectorStoreModule_ProvideVectorStoreManagerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VectorStoreManager provideVectorStoreManager() {
    return Preconditions.checkNotNullFromProvides(VectorStoreModule.INSTANCE.provideVectorStoreManager());
  }

  private static final class InstanceHolder {
    private static final VectorStoreModule_ProvideVectorStoreManagerFactory INSTANCE = new VectorStoreModule_ProvideVectorStoreManagerFactory();
  }
}
