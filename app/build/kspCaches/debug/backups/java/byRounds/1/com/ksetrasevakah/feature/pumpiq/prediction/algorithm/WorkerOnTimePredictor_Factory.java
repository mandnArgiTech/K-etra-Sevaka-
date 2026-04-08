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
public final class WorkerOnTimePredictor_Factory implements Factory<WorkerOnTimePredictor> {
  @Override
  public WorkerOnTimePredictor get() {
    return newInstance();
  }

  public static WorkerOnTimePredictor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WorkerOnTimePredictor newInstance() {
    return new WorkerOnTimePredictor();
  }

  private static final class InstanceHolder {
    private static final WorkerOnTimePredictor_Factory INSTANCE = new WorkerOnTimePredictor_Factory();
  }
}
