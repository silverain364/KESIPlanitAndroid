package com.example.kesi.data.module

import android.content.Context
import androidx.credentials.CredentialManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CredentialModule {
    @Provides
    @Singleton
    fun providerCredentialManager(@ApplicationContext context: Context): CredentialManager {
        return CredentialManager.create(context)
    }
}