package com.ksetrasevakah.core.vectorstore.di

import com.ksetrasevakah.core.vectorstore.EmbeddingGenerator
import com.ksetrasevakah.core.vectorstore.RagPipeline
import com.ksetrasevakah.core.vectorstore.VectorStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VectorStoreModule {

    @Provides
    @Singleton
    fun provideVectorStoreManager(): VectorStoreManager = VectorStoreManager()

    @Provides
    @Singleton
    fun provideEmbeddingGenerator(): EmbeddingGenerator = EmbeddingGenerator()

    @Provides
    @Singleton
    fun provideRagPipeline(
        embeddingGenerator: EmbeddingGenerator,
        vectorStoreManager: VectorStoreManager
    ): RagPipeline = RagPipeline(embeddingGenerator, vectorStoreManager)
}
