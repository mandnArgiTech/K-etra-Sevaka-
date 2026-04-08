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
public final class FaultPredictor_Factory implements Factory<FaultPredictor> {
  @Override
  public FaultPredictor get() {
    return newInstance();
  }

  public static FaultPredictor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FaultPredictor newInstance() {
    return new FaultPredictor();
  }

  private static final class InstanceHolder {
    private static final FaultPredictor_Factory INSTANCE = new FaultPredictor_Factory();
  }
}
