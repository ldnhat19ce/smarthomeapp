package com.ldnhat.smarthomeapp.data.di

import android.content.Context
import com.ldnhat.smarthomeapp.data.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAuthApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): AuthApi {
        return remoteDataSource.buildApi(AuthApi::class.java, context)
    }

    @Singleton
    @Provides
    fun provideDeviceApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): DeviceApi {
        return remoteDataSource.buildApi(DeviceApi::class.java, context)
    }

    @Singleton
    @Provides
    fun provideSpeechDataApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): SpeechDataApi {
        return remoteDataSource.buildApi(SpeechDataApi::class.java, context)
    }

    @Singleton
    @Provides
    fun provideDeviceTimerApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): DeviceTimerApi {
        return remoteDataSource.buildApi(DeviceTimerApi::class.java, context)
    }

    @Singleton
    @Provides
    fun provideAccountApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): AccountApi {
        return remoteDataSource.buildApi(AccountApi::class.java, context)
    }

    @Singleton
    @Provides
    fun provideDeviceMonitorApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): DeviceMonitorApi {
        return remoteDataSource.buildApi(DeviceMonitorApi::class.java, context)
    }

    @Singleton
    @Provides
    fun provideDeviceTokenApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): DeviceTokenApi {
        return remoteDataSource.buildApi(DeviceTokenApi::class.java, context)
    }
}