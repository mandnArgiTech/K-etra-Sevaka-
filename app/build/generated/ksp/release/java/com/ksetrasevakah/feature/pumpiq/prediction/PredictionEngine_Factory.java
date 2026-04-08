package com.ksetrasevakah.feature.pumpiq.prediction;

import com.ksetrasevakah.core.database.dao.PredictionCacheDao;
import com.ksetrasevakah.core.domain.repository.FaultRepository;
import com.ksetrasevakah.core.domain.repository.TelemetryRepository;
import com.ksetrasevakah.core.domain.repository.WorkerActivityRepository;
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.FaultPredictor;
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.ForgotOffPredictor;
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.PowerFailurePredictor;
import com.ksetrasevakah.feature.pumpiq.prediction.algorithm.WorkerOnTimePredictor;
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
public final class PredictionEngine_Factory implements Factory<PredictionEngine> {
  private final Provider<TelemetryRepository> telemetryRepositoryProvider;

  private final Provider<FaultRepository> faultRepositoryProvider;

  private final Provider<WorkerActivityRepository> workerActivityRepositoryProvider;

  private final Provider<PredictionCacheDao> predictionCacheDaoProvider;

  private final Provider<PowerFailurePredictor> powerFailurePredictorProvider;

  private final Provider<FaultPredictor> faultPredictorProvider;

  private final Provider<WorkerOnTimePredictor> workerOnTimePredictorProvider;

  private final Provider<ForgotOffPredictor> forgotOffPredictorProvider;

  public PredictionEngine_Factory(Provider<TelemetryRepository> telemetryRepositoryProvider,
      Provider<FaultRepository> faultRepositoryProvider,
      Provider<WorkerActivityRepository> workerActivityRepositoryProvider,
      Provider<PredictionCacheDao> predictionCacheDaoProvider,
      Provider<PowerFailurePredictor> powerFailurePredictorProvider,
      Provider<FaultPredictor> faultPredictorProvider,
      Provider<WorkerOnTimePredictor> workerOnTimePredictorProvider,
      Provider<ForgotOffPredictor> forgotOffPredictorProvider) {
    this.telemetryRepositoryProvider = telemetryRepositoryProvider;
    this.faultRepositoryProvider = faultRepositoryProvider;
    this.workerActivityRepositoryProvider = workerActivityRepositoryProvider;
    this.predictionCacheDaoProvider = predictionCacheDaoProvider;
    this.powerFailurePredictorProvider = powerFailurePredictorProvider;
    this.faultPredictorProvider = faultPredictorProvider;
    this.workerOnTimePredictorProvider = workerOnTimePredictorProvider;
    this.forgotOffPredictorProvider = forgotOffPredictorProvider;
  }

  @Override
  public PredictionEngine get() {
    return newInstance(telemetryRepositoryProvider.get(), faultRepositoryProvider.get(), workerActivityRepositoryProvider.get(), predictionCacheDaoProvider.get(), powerFailurePredictorProvider.get(), faultPredictorProvider.get(), workerOnTimePredictorProvider.get(), forgotOffPredictorProvider.get());
  }

  public static PredictionEngine_Factory create(
      Provider<TelemetryRepository> telemetryRepositoryProvider,
      Provider<FaultRepository> faultRepositoryProvider,
      Provider<WorkerActivityRepository> workerActivityRepositoryProvider,
      Provider<PredictionCacheDao> predictionCacheDaoProvider,
      Provider<PowerFailurePredictor> powerFailurePredictorProvider,
      Provider<FaultPredictor> faultPredictorProvider,
      Provider<WorkerOnTimePredictor> workerOnTimePredictorProvider,
      Provider<ForgotOffPredictor> forgotOffPredictorProvider) {
    return new PredictionEngine_Factory(telemetryRepositoryProvider, faultRepositoryProvider, workerActivityRepositoryProvider, predictionCacheDaoProvider, powerFailurePredictorProvider, faultPredictorProvider, workerOnTimePredictorProvider, forgotOffPredictorProvider);
  }

  public static PredictionEngine newInstance(TelemetryRepository telemetryRepository,
      FaultRepository faultRepository, WorkerActivityRepository workerActivityRepository,
      PredictionCacheDao predictionCacheDao, PowerFailurePredictor powerFailurePredictor,
      FaultPredictor faultPredictor, WorkerOnTimePredictor workerOnTimePredictor,
      ForgotOffPredictor forgotOffPredictor) {
    return new PredictionEngine(telemetryRepository, faultRepository, workerActivityRepository, predictionCacheDao, powerFailurePredictor, faultPredictor, workerOnTimePredictor, forgotOffPredictor);
  }
}
