package com.ksetrasevakah.feature.suraksha.prediction;

import com.ksetrasevakah.core.ai.MlcLlmEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ThreatClassifier_Factory implements Factory<ThreatClassifier> {
  private final Provider<MlcLlmEngine> engineProvider;

  private final Provider<ActivitySpikeDetector> spikeDetectorProvider;

  public ThreatClassifier_Factory(Provider<MlcLlmEngine> engineProvider,
      Provider<ActivitySpikeDetector> spikeDetectorProvider) {
    this.engineProvider = engineProvider;
    this.spikeDetectorProvider = spikeDetectorProvider;
  }

  @Override
  public ThreatClassifier get() {
    return newInstance(engineProvider.get(), spikeDetectorProvider.get());
  }

  public static ThreatClassifier_Factory create(Provider<MlcLlmEngine> engineProvider,
      Provider<ActivitySpikeDetector> spikeDetectorProvider) {
    return new ThreatClassifier_Factory(engineProvider, spikeDetectorProvider);
  }

  public static ThreatClassifier newInstance(MlcLlmEngine engine,
      ActivitySpikeDetector spikeDetector) {
    return new ThreatClassifier(engine, spikeDetector);
  }
}
