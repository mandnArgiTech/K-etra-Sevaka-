package com.ksetrasevakah.core.ai;

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
public final class DefaultMlcLlmEngine_Factory implements Factory<DefaultMlcLlmEngine> {
  @Override
  public DefaultMlcLlmEngine get() {
    return newInstance();
  }

  public static DefaultMlcLlmEngine_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DefaultMlcLlmEngine newInstance() {
    return new DefaultMlcLlmEngine();
  }

  private static final class InstanceHolder {
    private static final DefaultMlcLlmEngine_Factory INSTANCE = new DefaultMlcLlmEngine_Factory();
  }
}
