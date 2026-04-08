package com.ksetrasevakah.core.di

import com.ksetrasevakah.core.data.repository.ChatRepositoryImpl
import com.ksetrasevakah.core.data.repository.FaultRepositoryImpl
import com.ksetrasevakah.core.data.repository.MotorStateRepositoryImpl
import com.ksetrasevakah.core.data.repository.PredictionRepositoryImpl
import com.ksetrasevakah.core.data.repository.TelemetryRepositoryImpl
import com.ksetrasevakah.core.data.repository.WorkerActivityRepositoryImpl
import com.ksetrasevakah.core.domain.repository.ChatRepository
import com.ksetrasevakah.core.domain.repository.FaultRepository
import com.ksetrasevakah.core.domain.repository.MotorStateRepository
import com.ksetrasevakah.core.domain.repository.PredictionRepository
import com.ksetrasevakah.core.domain.repository.TelemetryRepository
import com.ksetrasevakah.core.domain.repository.WorkerActivityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMotorStateRepository(impl: MotorStateRepositoryImpl): MotorStateRepository

    @Binds
    @Singleton
    abstract fun bindTelemetryRepository(impl: TelemetryRepositoryImpl): TelemetryRepository

    @Binds
    @Singleton
    abstract fun bindFaultRepository(impl: FaultRepositoryImpl): FaultRepository

    @Binds
    @Singleton
    abstract fun bindWorkerActivityRepository(impl: WorkerActivityRepositoryImpl): WorkerActivityRepository

    @Binds
    @Singleton
    abstract fun bindPredictionRepository(impl: PredictionRepositoryImpl): PredictionRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
