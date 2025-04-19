package com.example.a041_stellarforecast.di

import com.example.a041_stellarforecast.data.network.WeatherClient
import com.example.a041_stellarforecast.data.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Singleton
    @Provides
    fun provideWeatherRepository(dataClient: WeatherClient): WeatherRepository {
        return WeatherRepository(dataClient)
    }
}