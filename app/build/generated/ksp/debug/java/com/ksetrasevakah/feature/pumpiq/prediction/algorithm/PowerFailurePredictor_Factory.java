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
public final class PowerFailurePredictor_Factory implements Factory<PowerFailurePredictor> {
  @Override
  public PowerFailurePredictor get() {
    return newInstance();
  }

  public static PowerFailurePredictor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PowerFailurePredictor newInstance() {
    return new PowerFailurePredictor();
  }

  private static final class InstanceHolder {
    private static final PowerFailurePredictor_Factory INSTANCE = new PowerFailurePredictor_Factory();
  }
}
