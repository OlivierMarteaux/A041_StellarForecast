package com.example.a041_stellarforecast.di

import com.example.a041_stellarforecast.data.network.WeatherClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * la classe  NetworkModule  joue un rôle crucial dans la mise en place de la communication réseau
 * au sein de votre application Android. Elle est annotée avec@Modulepour indiquer à Hilt
 * (injection de dépendances) à qui elle fournit des dépendances pour l'application.
 * L'annotation@InstallIn(ApplicationComponent::class)spécifie que les dépendances fournies
 * par ce module seront disponibles au niveau du composant d'application, ce qui signifie
 * qu'elles seront accessibles pendant toute la durée de vie de l'application. Elle fournit des
 * instances de Retrofit et de l'interface WeatherClient. La première fonction fournit l'instance
 * de Retrofit, tandis que la seconde fournit le service de l'API météo.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    // Provides a singleton instance of Retrofit for network communication
    /**
     * Cette fonction fournit une instance unique de Retrofit pour la communication réseau.
     * Elle utilise Moshi comme convertisseur JSON, avec l'ajout du KotlinJsonAdapterFactory
     * pour une meilleure compatibilité avec les classes Kotlin. La configuration d'OkHttpClient
     * est extraite dans une fonction séparée pour plus de clarté ;
     */
    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .client(provideOkHttpClient()) // Uses a separate function for OkHttpClient configuration
            .build()
    }


    // Provides a singleton instance of WeatherClient using Retrofit
    @Singleton
    @Provides
    fun provideWeatherClient(retrofit: Retrofit): WeatherClient {
        return retrofit.create(WeatherClient::class.java)
    }


    // Private function to configure OkHttpClient with an interceptor for logging
    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }.build()
    }
}