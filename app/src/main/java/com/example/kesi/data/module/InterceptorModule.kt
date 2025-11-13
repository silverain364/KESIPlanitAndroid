package com.example.kesi.data.module

import com.example.kesi.data.local.TokenManager
import com.example.kesi.util.network.RequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InterceptorModule {
    @Provides
    @Singleton
    fun providerRequestInterceptor(tokenManager: TokenManager): Interceptor {
        return RequestInterceptor(tokenManager)
    }
}