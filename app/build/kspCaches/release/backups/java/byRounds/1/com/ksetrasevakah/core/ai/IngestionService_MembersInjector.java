package com.ksetrasevakah.core.ai;

import com.ksetrasevakah.core.database.dao.TelemetryDao;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class IngestionService_MembersInjector implements MembersInjector<IngestionService> {
  private final Provider<MlcLlmEngine> engineProvider;

  private final Provider<TelemetryDao> telemetryDaoProvider;

  public IngestionService_MembersInjector(Provider<MlcLlmEngine> engineProvider,
      Provider<TelemetryDao> telemetryDaoProvider) {
    this.engineProvider = engineProvider;
    this.telemetryDaoProvider = telemetryDaoProvider;
  }

  public static MembersInjector<IngestionService> create(Provider<MlcLlmEngine> engineProvider,
      Provider<TelemetryDao> telemetryDaoProvider) {
    return new IngestionService_MembersInjector(engineProvider, telemetryDaoProvider);
  }

  @Override
  public void injectMembers(IngestionService instance) {
    injectEngine(instance, engineProvider.get());
    injectTelemetryDao(instance, telemetryDaoProvider.get());
  }

  @InjectedFieldSignature("com.ksetrasevakah.core.ai.IngestionService.engine")
  public static void injectEngine(IngestionService instance, MlcLlmEngine engine) {
    instance.engine = engine;
  }

  @InjectedFieldSignature("com.ksetrasevakah.core.ai.IngestionService.telemetryDao")
  public static void injectTelemetryDao(IngestionService instance, TelemetryDao telemetryDao) {
    instance.telemetryDao = telemetryDao;
  }
}
