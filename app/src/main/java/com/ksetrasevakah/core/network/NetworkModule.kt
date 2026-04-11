package com.ksetrasevakah.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        huggingFaceReadTokenProvider: HuggingFaceReadTokenProvider
    ): OkHttpClient {
        val huggingFaceAuthInterceptor = Interceptor { chain ->
            val req = chain.request()
            val url = req.url.toString()
            val token = huggingFaceReadTokenProvider.authorizationBearerOrNull()
            val newReq = if (token != null && url.contains("huggingface.co", ignoreCase = true)) {
                req.newBuilder().header("Authorization", "Bearer $token").build()
            } else {
                req
            }
            chain.proceed(newReq)
        }
        return OkHttpClient.Builder()
            .addInterceptor(huggingFaceAuthInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build()
    }
}
