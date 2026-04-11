package com.ksetrasevakah.core.vectorstore.di

import com.ksetrasevakah.core.database.dao.VectorDocumentDao
import com.ksetrasevakah.core.vectorstore.EmbeddingDownloader
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
    fun provideVectorStoreManager(dao: VectorDocumentDao): VectorStoreManager =
        VectorStoreManager(dao)

    @Provides
    @Singleton
    fun provideEmbeddingGenerator(downloader: EmbeddingDownloader): EmbeddingGenerator =
        EmbeddingGenerator(downloader)

    @Provides
    @Singleton
    fun provideRagPipeline(
        embeddingGenerator: EmbeddingGenerator,
        vectorStoreManager: VectorStoreManager
    ): RagPipeline = RagPipeline(embeddingGenerator, vectorStoreManager)
}
