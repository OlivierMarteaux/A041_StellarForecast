package com.example.a041_stellarforecast.data.repository

import android.util.Log
import com.example.a041_stellarforecast.data.network.WeatherClient
import com.example.a041_stellarforecast.domain.model.WeatherReportModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class WeatherRepository(private val dataService: WeatherClient) {
    private val API_KEY = "bc471261722097245973655b2459651a"

    fun fetchForecastData(lat: Double, lng: Double): Flow<List<WeatherReportModel>> =
        flow {

            val result = dataService.getWeatherByPosition(
                latitude = lat,
                longitude = lng,
                apiKey = API_KEY
            )
            val model = result.body()?.toDomainModel() ?: throw Exception("Invalid data")
            emit(model)
        }.catch { error ->
            Log.e("WeatherRepository", error.message ?: "")
        }
}