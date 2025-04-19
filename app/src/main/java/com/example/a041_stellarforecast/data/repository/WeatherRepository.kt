package com.example.a041_stellarforecast.data.repository

import android.util.Log
import com.example.a041_stellarforecast.data.network.WeatherClient
import com.example.a041_stellarforecast.domain.model.WeatherReportModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Agit comme un gardien des prévisions météorologiques, interagissant avec Retrofit et donc notre
 * API pour récupérer ces données et les rendre accessibles à d'autres parties de l'application.
 * Définit la clé API nécessaire pour l'interrogation du service météorologique distant.
 */
class WeatherRepository(private val dataService: WeatherClient) {
    private val API_KEY = "bc471261722097245973655b2459651a"

    /**
     * Prend en paramètres la latitude (lat) et la longitude (lng) pour spécifier l'emplacement
     * désiré. Utilise un flux (flow) pour gérer les données de manière asynchrone, créant ainsi
     * une coroutine. À chaque fois qu’une nouvelle donnée sera disponible, nous n’aurons qu'à
     * émettre avec la fonction “emit” propre au “flow”.
     */
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