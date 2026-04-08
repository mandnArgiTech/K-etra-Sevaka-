package com.ksetrasevakah.feature.pumpiq.prediction.algorithm;

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
public final class ForgotOffPredictor_Factory implements Factory<ForgotOffPredictor> {
  @Override
  public ForgotOffPredictor get() {
    return newInstance();
  }

  public static ForgotOffPredictor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ForgotOffPredictor newInstance() {
    return new ForgotOffPredictor();
  }

  private static final class InstanceHolder {
    private static final ForgotOffPredictor_Factory INSTANCE = new ForgotOffPredictor_Factory();
  }
}
