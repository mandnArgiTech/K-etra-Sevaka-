package com.ksetrasevakah.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.telephony.SmsManager;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.ksetrasevakah.core.ai.DefaultMlcLlmEngine;
import com.ksetrasevakah.core.ai.IngestionService;
import com.ksetrasevakah.core.ai.IngestionService_MembersInjector;
import com.ksetrasevakah.core.ai.MlcLlmEngine;
import com.ksetrasevakah.core.ai.ModelManager;
import com.ksetrasevakah.core.ai.di.AiModule_Companion_ProvideModelManagerFactory;
import com.ksetrasevakah.core.backup.BackupManager;
import com.ksetrasevakah.core.backup.DriveApiClient;
import com.ksetrasevakah.core.backup.di.BackupModule_ProvideBackupManagerFactory;
import com.ksetrasevakah.core.backup.di.BackupModule_ProvideDriveApiClientFactory;
import com.ksetrasevakah.core.data.repository.ChatRepositoryImpl;
import com.ksetrasevakah.core.data.repository.MotorStateRepositoryImpl;
import com.ksetrasevakah.core.data.repository.PredictionRepositoryImpl;
import com.ksetrasevakah.core.data.repository.TelemetryRepositoryImpl;
import com.ksetrasevakah.core.database.KsetraDatabase;
import com.ksetrasevakah.core.database.dao.BackupLogDao;
import com.ksetrasevakah.core.database.dao.CameraConfigDao;
import com.ksetrasevakah.core.database.dao.ChatMessageDao;
import com.ksetrasevakah.core.database.dao.ChatThreadDao;
import com.ksetrasevakah.core.database.dao.MotorStateDao;
import com.ksetrasevakah.core.database.dao.PredictionCacheDao;
import com.ksetrasevakah.core.database.dao.SecurityEventDao;
import com.ksetrasevakah.core.database.dao.TelemetryDao;
import com.ksetrasevakah.core.database.di.DatabaseModule_ProvideBackupLogDaoFactory;
import com.ksetrasevakah.core.database.di.DatabaseModule_ProvideCameraConfigDaoFactory;
import com.ksetrasevakah.core.database.di.DatabaseModule_ProvideChatMessageDaoFactory;
import com.ksetrasevakah.core.database.di.DatabaseModule_ProvideChatThreadDaoFactory;
import com.ksetrasevakah.core.database.di.DatabaseModule_ProvideDatabaseFactory;
import com.ksetrasevakah.core.database.di.DatabaseModule_ProvideMotorStateDaoFactory;
import com.ksetrasevakah.core.database.di.DatabaseModule_ProvidePredictionCacheDaoFactory;
import com.ksetrasevakah.core.database.di.DatabaseModule_ProvideSecurityEventDaoFactory;
import com.ksetrasevakah.core.database.di.DatabaseModule_ProvideTelemetryDaoFactory;
import com.ksetrasevakah.core.di.AppModule_ProvideApplicationContextFactory;
import com.ksetrasevakah.core.domain.repository.ChatRepository;
import com.ksetrasevakah.core.domain.repository.MotorStateRepository;
import com.ksetrasevakah.core.domain.repository.PredictionRepository;
import com.ksetrasevakah.core.domain.repository.TelemetryRepository;
import com.ksetrasevakah.core.notification.CriticalAlarmManager;
import com.ksetrasevakah.core.notification.KsetraNotificationManager;
import com.ksetrasevakah.core.notification.TapoNotificationListener;
import com.ksetrasevakah.core.notification.TapoNotificationListener_MembersInjector;
import com.ksetrasevakah.core.notification.di.NotificationModule_ProvideKsetraNotificationManagerFactory;
import com.ksetrasevakah.core.sms.DefaultSmsCommandSender;
import com.ksetrasevakah.core.sms.SmsCommandSender;
import com.ksetrasevakah.core.sms.di.SmsModule_Companion_ProvideSmsManagerFactory;
import com.ksetrasevakah.core.vectorstore.EmbeddingGenerator;
import com.ksetrasevakah.core.vectorstore.RagPipeline;
import com.ksetrasevakah.core.vectorstore.VectorStoreManager;
import com.ksetrasevakah.core.vectorstore.di.VectorStoreModule_ProvideEmbeddingGeneratorFactory;
import com.ksetrasevakah.core.vectorstore.di.VectorStoreModule_ProvideRagPipelineFactory;
import com.ksetrasevakah.core.vectorstore.di.VectorStoreModule_ProvideVectorStoreManagerFactory;
import com.ksetrasevakah.feature.hub.HubViewModel;
import com.ksetrasevakah.feature.hub.HubViewModel_HiltModules;
import com.ksetrasevakah.feature.hub.HubViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ksetrasevakah.feature.hub.HubViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ksetrasevakah.feature.pumpiq.chat.ChatOrchestrator;
import com.ksetrasevakah.feature.pumpiq.chat.ChatViewModel;
import com.ksetrasevakah.feature.pumpiq.chat.ChatViewModel_HiltModules;
import com.ksetrasevakah.feature.pumpiq.chat.ChatViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ksetrasevakah.feature.pumpiq.chat.ChatViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.CreateChatThreadUseCase;
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.GetChatMessagesUseCase;
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.GetChatThreadsUseCase;
import com.ksetrasevakah.feature.pumpiq.chat.domain.usecase.SendChatMessageUseCase;
import com.ksetrasevakah.feature.pumpiq.chat.prompt.ContextAssembler;
import com.ksetrasevakah.feature.pumpiq.chat.prompt.SystemPromptBuilder;
import com.ksetrasevakah.feature.pumpiq.dashboard.DashboardViewModel;
import com.ksetrasevakah.feature.pumpiq.dashboard.DashboardViewModel_HiltModules;
import com.ksetrasevakah.feature.pumpiq.dashboard.DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ksetrasevakah.feature.pumpiq.dashboard.DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ksetrasevakah.feature.pumpiq.domain.usecase.SendSmsCommandUseCase;
import com.ksetrasevakah.feature.settings.SettingsViewModel;
import com.ksetrasevakah.feature.settings.SettingsViewModel_HiltModules;
import com.ksetrasevakah.feature.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ksetrasevakah.feature.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ksetrasevakah.feature.suraksha.camera.CameraMatrixViewModel;
import com.ksetrasevakah.feature.suraksha.camera.CameraMatrixViewModel_HiltModules;
import com.ksetrasevakah.feature.suraksha.camera.CameraMatrixViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ksetrasevakah.feature.suraksha.camera.CameraMatrixViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ksetrasevakah.feature.suraksha.dashboard.SurakshaDashboardViewModel;
import com.ksetrasevakah.feature.suraksha.dashboard.SurakshaDashboardViewModel_HiltModules;
import com.ksetrasevakah.feature.suraksha.dashboard.SurakshaDashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ksetrasevakah.feature.suraksha.dashboard.SurakshaDashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ksetrasevakah.feature.suraksha.data.repository.CameraConfigRepositoryImpl;
import com.ksetrasevakah.feature.suraksha.data.repository.SecurityEventRepositoryImpl;
import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository;
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository;
import com.ksetrasevakah.feature.suraksha.prediction.ActivitySpikeDetector;
import com.ksetrasevakah.feature.suraksha.prediction.SecurityBriefingGenerator;
import com.ksetrasevakah.feature.suraksha.prediction.ThreatClassifier;
import com.ksetrasevakah.feature.suraksha.prediction.ThreatRouter;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerKsetraSevakahApp_HiltComponents_SingletonC {
  private DaggerKsetraSevakahApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public KsetraSevakahApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements KsetraSevakahApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public KsetraSevakahApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements KsetraSevakahApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public KsetraSevakahApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements KsetraSevakahApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public KsetraSevakahApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements KsetraSevakahApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public KsetraSevakahApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements KsetraSevakahApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public KsetraSevakahApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements KsetraSevakahApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public KsetraSevakahApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements KsetraSevakahApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public KsetraSevakahApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends KsetraSevakahApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends KsetraSevakahApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends KsetraSevakahApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends KsetraSevakahApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(6).put(CameraMatrixViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CameraMatrixViewModel_HiltModules.KeyModule.provide()).put(ChatViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ChatViewModel_HiltModules.KeyModule.provide()).put(DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DashboardViewModel_HiltModules.KeyModule.provide()).put(HubViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HubViewModel_HiltModules.KeyModule.provide()).put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()).put(SurakshaDashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SurakshaDashboardViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends KsetraSevakahApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<CameraMatrixViewModel> cameraMatrixViewModelProvider;

    private Provider<ChatViewModel> chatViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<HubViewModel> hubViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<SurakshaDashboardViewModel> surakshaDashboardViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private GetChatThreadsUseCase getChatThreadsUseCase() {
      return new GetChatThreadsUseCase(singletonCImpl.bindChatRepositoryProvider.get());
    }

    private GetChatMessagesUseCase getChatMessagesUseCase() {
      return new GetChatMessagesUseCase(singletonCImpl.bindChatRepositoryProvider.get());
    }

    private SendChatMessageUseCase sendChatMessageUseCase() {
      return new SendChatMessageUseCase(singletonCImpl.bindChatRepositoryProvider.get());
    }

    private CreateChatThreadUseCase createChatThreadUseCase() {
      return new CreateChatThreadUseCase(singletonCImpl.bindChatRepositoryProvider.get());
    }

    private SystemPromptBuilder systemPromptBuilder() {
      return new SystemPromptBuilder(singletonCImpl.bindMotorStateRepositoryProvider.get(), singletonCImpl.bindPredictionRepositoryProvider.get(), singletonCImpl.provideRagPipelineProvider.get());
    }

    private ContextAssembler contextAssembler() {
      return new ContextAssembler(singletonCImpl.bindChatRepositoryProvider.get());
    }

    private ChatOrchestrator chatOrchestrator() {
      return new ChatOrchestrator(singletonCImpl.bindMlcLlmEngineProvider.get(), singletonCImpl.provideModelManagerProvider.get(), singletonCImpl.bindChatRepositoryProvider.get(), systemPromptBuilder(), contextAssembler());
    }

    private SendSmsCommandUseCase sendSmsCommandUseCase() {
      return new SendSmsCommandUseCase(singletonCImpl.bindSmsCommandSenderProvider.get(), singletonCImpl.motorStateDao());
    }

    private SecurityBriefingGenerator securityBriefingGenerator() {
      return new SecurityBriefingGenerator(singletonCImpl.bindMlcLlmEngineProvider.get(), singletonCImpl.bindSecurityEventRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.cameraMatrixViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.chatViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.hubViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.surakshaDashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(6).put(CameraMatrixViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) cameraMatrixViewModelProvider)).put(ChatViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) chatViewModelProvider)).put(DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) dashboardViewModelProvider)).put(HubViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) hubViewModelProvider)).put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)).put(SurakshaDashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) surakshaDashboardViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.ksetrasevakah.feature.suraksha.camera.CameraMatrixViewModel 
          return (T) new CameraMatrixViewModel(singletonCImpl.bindCameraConfigRepositoryProvider.get());

          case 1: // com.ksetrasevakah.feature.pumpiq.chat.ChatViewModel 
          return (T) new ChatViewModel(viewModelCImpl.getChatThreadsUseCase(), viewModelCImpl.getChatMessagesUseCase(), viewModelCImpl.sendChatMessageUseCase(), viewModelCImpl.createChatThreadUseCase(), viewModelCImpl.chatOrchestrator());

          case 2: // com.ksetrasevakah.feature.pumpiq.dashboard.DashboardViewModel 
          return (T) new DashboardViewModel(singletonCImpl.bindMotorStateRepositoryProvider.get(), singletonCImpl.bindTelemetryRepositoryProvider.get(), viewModelCImpl.sendSmsCommandUseCase());

          case 3: // com.ksetrasevakah.feature.hub.HubViewModel 
          return (T) new HubViewModel();

          case 4: // com.ksetrasevakah.feature.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.bindMotorStateRepositoryProvider.get(), singletonCImpl.provideBackupManagerProvider.get());

          case 5: // com.ksetrasevakah.feature.suraksha.dashboard.SurakshaDashboardViewModel 
          return (T) new SurakshaDashboardViewModel(singletonCImpl.bindSecurityEventRepositoryProvider.get(), viewModelCImpl.securityBriefingGenerator());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends KsetraSevakahApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends KsetraSevakahApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectIngestionService(IngestionService ingestionService) {
      injectIngestionService2(ingestionService);
    }

    @Override
    public void injectTapoNotificationListener(TapoNotificationListener tapoNotificationListener) {
      injectTapoNotificationListener2(tapoNotificationListener);
    }

    private IngestionService injectIngestionService2(IngestionService instance) {
      IngestionService_MembersInjector.injectEngine(instance, singletonCImpl.bindMlcLlmEngineProvider.get());
      IngestionService_MembersInjector.injectTelemetryDao(instance, singletonCImpl.telemetryDao());
      return instance;
    }

    private TapoNotificationListener injectTapoNotificationListener2(
        TapoNotificationListener instance2) {
      TapoNotificationListener_MembersInjector.injectThreatRouter(instance2, singletonCImpl.threatRouterProvider.get());
      return instance2;
    }
  }

  private static final class SingletonCImpl extends KsetraSevakahApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<KsetraDatabase> provideDatabaseProvider;

    private Provider<CameraConfigRepositoryImpl> cameraConfigRepositoryImplProvider;

    private Provider<CameraConfigRepository> bindCameraConfigRepositoryProvider;

    private Provider<ChatRepositoryImpl> chatRepositoryImplProvider;

    private Provider<ChatRepository> bindChatRepositoryProvider;

    private Provider<DefaultMlcLlmEngine> defaultMlcLlmEngineProvider;

    private Provider<MlcLlmEngine> bindMlcLlmEngineProvider;

    private Provider<ModelManager> provideModelManagerProvider;

    private Provider<MotorStateRepositoryImpl> motorStateRepositoryImplProvider;

    private Provider<MotorStateRepository> bindMotorStateRepositoryProvider;

    private Provider<PredictionRepositoryImpl> predictionRepositoryImplProvider;

    private Provider<PredictionRepository> bindPredictionRepositoryProvider;

    private Provider<EmbeddingGenerator> provideEmbeddingGeneratorProvider;

    private Provider<VectorStoreManager> provideVectorStoreManagerProvider;

    private Provider<RagPipeline> provideRagPipelineProvider;

    private Provider<TelemetryRepositoryImpl> telemetryRepositoryImplProvider;

    private Provider<TelemetryRepository> bindTelemetryRepositoryProvider;

    private Provider<SmsManager> provideSmsManagerProvider;

    private Provider<DefaultSmsCommandSender> defaultSmsCommandSenderProvider;

    private Provider<SmsCommandSender> bindSmsCommandSenderProvider;

    private Provider<DriveApiClient> provideDriveApiClientProvider;

    private Provider<BackupManager> provideBackupManagerProvider;

    private Provider<SecurityEventRepositoryImpl> securityEventRepositoryImplProvider;

    private Provider<SecurityEventRepository> bindSecurityEventRepositoryProvider;

    private Provider<ActivitySpikeDetector> activitySpikeDetectorProvider;

    private Provider<ThreatClassifier> threatClassifierProvider;

    private Provider<KsetraNotificationManager> provideKsetraNotificationManagerProvider;

    private Provider<Context> provideApplicationContextProvider;

    private Provider<CriticalAlarmManager> criticalAlarmManagerProvider;

    private Provider<ThreatRouter> threatRouterProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);
      initialize2(applicationContextModuleParam);

    }

    private CameraConfigDao cameraConfigDao() {
      return DatabaseModule_ProvideCameraConfigDaoFactory.provideCameraConfigDao(provideDatabaseProvider.get());
    }

    private ChatThreadDao chatThreadDao() {
      return DatabaseModule_ProvideChatThreadDaoFactory.provideChatThreadDao(provideDatabaseProvider.get());
    }

    private ChatMessageDao chatMessageDao() {
      return DatabaseModule_ProvideChatMessageDaoFactory.provideChatMessageDao(provideDatabaseProvider.get());
    }

    private MotorStateDao motorStateDao() {
      return DatabaseModule_ProvideMotorStateDaoFactory.provideMotorStateDao(provideDatabaseProvider.get());
    }

    private PredictionCacheDao predictionCacheDao() {
      return DatabaseModule_ProvidePredictionCacheDaoFactory.providePredictionCacheDao(provideDatabaseProvider.get());
    }

    private TelemetryDao telemetryDao() {
      return DatabaseModule_ProvideTelemetryDaoFactory.provideTelemetryDao(provideDatabaseProvider.get());
    }

    private BackupLogDao backupLogDao() {
      return DatabaseModule_ProvideBackupLogDaoFactory.provideBackupLogDao(provideDatabaseProvider.get());
    }

    private SecurityEventDao securityEventDao() {
      return DatabaseModule_ProvideSecurityEventDaoFactory.provideSecurityEventDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<KsetraDatabase>(singletonCImpl, 1));
      this.cameraConfigRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 0);
      this.bindCameraConfigRepositoryProvider = DoubleCheck.provider((Provider) cameraConfigRepositoryImplProvider);
      this.chatRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 2);
      this.bindChatRepositoryProvider = DoubleCheck.provider((Provider) chatRepositoryImplProvider);
      this.defaultMlcLlmEngineProvider = new SwitchingProvider<>(singletonCImpl, 3);
      this.bindMlcLlmEngineProvider = DoubleCheck.provider((Provider) defaultMlcLlmEngineProvider);
      this.provideModelManagerProvider = DoubleCheck.provider(new SwitchingProvider<ModelManager>(singletonCImpl, 4));
      this.motorStateRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 5);
      this.bindMotorStateRepositoryProvider = DoubleCheck.provider((Provider) motorStateRepositoryImplProvider);
      this.predictionRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 6);
      this.bindPredictionRepositoryProvider = DoubleCheck.provider((Provider) predictionRepositoryImplProvider);
      this.provideEmbeddingGeneratorProvider = DoubleCheck.provider(new SwitchingProvider<EmbeddingGenerator>(singletonCImpl, 8));
      this.provideVectorStoreManagerProvider = DoubleCheck.provider(new SwitchingProvider<VectorStoreManager>(singletonCImpl, 9));
      this.provideRagPipelineProvider = DoubleCheck.provider(new SwitchingProvider<RagPipeline>(singletonCImpl, 7));
      this.telemetryRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 10);
      this.bindTelemetryRepositoryProvider = DoubleCheck.provider((Provider) telemetryRepositoryImplProvider);
      this.provideSmsManagerProvider = DoubleCheck.provider(new SwitchingProvider<SmsManager>(singletonCImpl, 12));
      this.defaultSmsCommandSenderProvider = new SwitchingProvider<>(singletonCImpl, 11);
      this.bindSmsCommandSenderProvider = DoubleCheck.provider((Provider) defaultSmsCommandSenderProvider);
      this.provideDriveApiClientProvider = DoubleCheck.provider(new SwitchingProvider<DriveApiClient>(singletonCImpl, 14));
      this.provideBackupManagerProvider = DoubleCheck.provider(new SwitchingProvider<BackupManager>(singletonCImpl, 13));
      this.securityEventRepositoryImplProvider = new SwitchingProvider<>(singletonCImpl, 15);
      this.bindSecurityEventRepositoryProvider = DoubleCheck.provider((Provider) securityEventRepositoryImplProvider);
      this.activitySpikeDetectorProvider = DoubleCheck.provider(new SwitchingProvider<ActivitySpikeDetector>(singletonCImpl, 18));
    }

    @SuppressWarnings("unchecked")
    private void initialize2(final ApplicationContextModule applicationContextModuleParam) {
      this.threatClassifierProvider = DoubleCheck.provider(new SwitchingProvider<ThreatClassifier>(singletonCImpl, 17));
      this.provideKsetraNotificationManagerProvider = DoubleCheck.provider(new SwitchingProvider<KsetraNotificationManager>(singletonCImpl, 19));
      this.provideApplicationContextProvider = DoubleCheck.provider(new SwitchingProvider<Context>(singletonCImpl, 21));
      this.criticalAlarmManagerProvider = DoubleCheck.provider(new SwitchingProvider<CriticalAlarmManager>(singletonCImpl, 20));
      this.threatRouterProvider = DoubleCheck.provider(new SwitchingProvider<ThreatRouter>(singletonCImpl, 16));
    }

    @Override
    public void injectKsetraSevakahApp(KsetraSevakahApp ksetraSevakahApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.ksetrasevakah.feature.suraksha.data.repository.CameraConfigRepositoryImpl 
          return (T) new CameraConfigRepositoryImpl(singletonCImpl.cameraConfigDao());

          case 1: // com.ksetrasevakah.core.database.KsetraDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.ksetrasevakah.core.data.repository.ChatRepositoryImpl 
          return (T) new ChatRepositoryImpl(singletonCImpl.chatThreadDao(), singletonCImpl.chatMessageDao());

          case 3: // com.ksetrasevakah.core.ai.DefaultMlcLlmEngine 
          return (T) new DefaultMlcLlmEngine();

          case 4: // com.ksetrasevakah.core.ai.ModelManager 
          return (T) AiModule_Companion_ProvideModelManagerFactory.provideModelManager(singletonCImpl.bindMlcLlmEngineProvider.get());

          case 5: // com.ksetrasevakah.core.data.repository.MotorStateRepositoryImpl 
          return (T) new MotorStateRepositoryImpl(singletonCImpl.motorStateDao());

          case 6: // com.ksetrasevakah.core.data.repository.PredictionRepositoryImpl 
          return (T) new PredictionRepositoryImpl(singletonCImpl.predictionCacheDao());

          case 7: // com.ksetrasevakah.core.vectorstore.RagPipeline 
          return (T) VectorStoreModule_ProvideRagPipelineFactory.provideRagPipeline(singletonCImpl.provideEmbeddingGeneratorProvider.get(), singletonCImpl.provideVectorStoreManagerProvider.get());

          case 8: // com.ksetrasevakah.core.vectorstore.EmbeddingGenerator 
          return (T) VectorStoreModule_ProvideEmbeddingGeneratorFactory.provideEmbeddingGenerator();

          case 9: // com.ksetrasevakah.core.vectorstore.VectorStoreManager 
          return (T) VectorStoreModule_ProvideVectorStoreManagerFactory.provideVectorStoreManager();

          case 10: // com.ksetrasevakah.core.data.repository.TelemetryRepositoryImpl 
          return (T) new TelemetryRepositoryImpl(singletonCImpl.telemetryDao());

          case 11: // com.ksetrasevakah.core.sms.DefaultSmsCommandSender 
          return (T) new DefaultSmsCommandSender(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideSmsManagerProvider.get());

          case 12: // android.telephony.SmsManager 
          return (T) SmsModule_Companion_ProvideSmsManagerFactory.provideSmsManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 13: // com.ksetrasevakah.core.backup.BackupManager 
          return (T) BackupModule_ProvideBackupManagerFactory.provideBackupManager(singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.backupLogDao(), singletonCImpl.provideDriveApiClientProvider.get());

          case 14: // com.ksetrasevakah.core.backup.DriveApiClient 
          return (T) BackupModule_ProvideDriveApiClientFactory.provideDriveApiClient();

          case 15: // com.ksetrasevakah.feature.suraksha.data.repository.SecurityEventRepositoryImpl 
          return (T) new SecurityEventRepositoryImpl(singletonCImpl.securityEventDao());

          case 16: // com.ksetrasevakah.feature.suraksha.prediction.ThreatRouter 
          return (T) new ThreatRouter(singletonCImpl.threatClassifierProvider.get(), singletonCImpl.bindSecurityEventRepositoryProvider.get(), singletonCImpl.bindCameraConfigRepositoryProvider.get(), singletonCImpl.provideKsetraNotificationManagerProvider.get(), singletonCImpl.criticalAlarmManagerProvider.get());

          case 17: // com.ksetrasevakah.feature.suraksha.prediction.ThreatClassifier 
          return (T) new ThreatClassifier(singletonCImpl.bindMlcLlmEngineProvider.get(), singletonCImpl.activitySpikeDetectorProvider.get());

          case 18: // com.ksetrasevakah.feature.suraksha.prediction.ActivitySpikeDetector 
          return (T) new ActivitySpikeDetector(singletonCImpl.bindSecurityEventRepositoryProvider.get());

          case 19: // com.ksetrasevakah.core.notification.KsetraNotificationManager 
          return (T) NotificationModule_ProvideKsetraNotificationManagerFactory.provideKsetraNotificationManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 20: // com.ksetrasevakah.core.notification.CriticalAlarmManager 
          return (T) new CriticalAlarmManager(singletonCImpl.provideApplicationContextProvider.get(), singletonCImpl.provideKsetraNotificationManagerProvider.get());

          case 21: // android.content.Context 
          return (T) AppModule_ProvideApplicationContextFactory.provideApplicationContext(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
