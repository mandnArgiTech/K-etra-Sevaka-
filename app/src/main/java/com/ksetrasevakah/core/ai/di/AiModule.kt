package com.ksetrasevakah.core.ai.di

import com.ksetrasevakah.core.ai.LiteRtLmEngine
import com.ksetrasevakah.core.ai.MlcLlmEngine
import com.ksetrasevakah.core.ai.ModelManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindMlcLlmEngine(impl: LiteRtLmEngine): MlcLlmEngine

    companion object {
        @Provides
        @Singleton
        fun provideModelManager(engine: MlcLlmEngine): ModelManager =
            ModelManager(engine, CoroutineScope(SupervisorJob() + Dispatchers.Default))
    }
}
