package com.example.kesi.data.module

import com.example.kesi.data.remote.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideAlarmApi(retrofit: Retrofit): AlarmApi =
        retrofit.create(AlarmApi::class.java)

    @Provides
    @Singleton
    fun provideFcmApi(retrofit: Retrofit): FCMApi =
        retrofit.create(FCMApi::class.java)

    @Provides
    @Singleton
    fun provideFriendsApi(retrofit: Retrofit): FriendsApi =
        retrofit.create(FriendsApi::class.java)

    @Provides
    @Singleton
    fun provideGroupApi(retrofit: Retrofit): GroupApi =
        retrofit.create(GroupApi::class.java)

    @Provides
    @Singleton
    fun provideGroupScheduleApi(retrofit: Retrofit): GroupScheduleApi =
        retrofit.create(GroupScheduleApi::class.java)

    @Provides
    @Singleton
    fun providePersonalScheduleApi(retrofit: Retrofit): PersonalScheduleApi =
        retrofit.create(PersonalScheduleApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)
}