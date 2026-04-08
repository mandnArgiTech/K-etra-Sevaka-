package com.ksetrasevakah.feature.suraksha.di

import com.ksetrasevakah.feature.suraksha.data.repository.CameraConfigRepositoryImpl
import com.ksetrasevakah.feature.suraksha.data.repository.SecurityEventRepositoryImpl
import com.ksetrasevakah.feature.suraksha.domain.repository.CameraConfigRepository
import com.ksetrasevakah.feature.suraksha.domain.repository.SecurityEventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SurakshaModule {

    @Binds
    @Singleton
    abstract fun bindSecurityEventRepository(
        impl: SecurityEventRepositoryImpl
    ): SecurityEventRepository

    @Binds
    @Singleton
    abstract fun bindCameraConfigRepository(
        impl: CameraConfigRepositoryImpl
    ): CameraConfigRepository
}
