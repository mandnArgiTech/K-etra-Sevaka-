package com.ksetrasevakah.core.di

import com.ksetrasevakah.core.data.repository.DataDiagnosticsRepositoryImpl
import com.ksetrasevakah.core.domain.repository.DataDiagnosticsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataDiagnosticsModule {

    @Provides
    @Singleton
    fun provideDataDiagnosticsRepository(
        impl: DataDiagnosticsRepositoryImpl
    ): DataDiagnosticsRepository = impl
}
