package com.example.a041_stellarforecast.data.response

import com.example.a041_stellarforecast.domain.model.WeatherReportModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Calendar

/**
 * Cette classe principale représente la réponse complète de l'API OpenWeather pour les
 * prévisions météorologiques. Elle contient une liste de prévisions ( forecasts  ),
 * chaque prévision étant représentée par la classe interne  ForecastResponse   .
 *
 * En résumé, cette classe Kotlin fournit une représentation structurée des données reçues
 * de l'API OpenWeather, et l'utilisation des annotations @JsonClass  permet de simplifier
 * la sérialisation et la désérialisation JSON via Moshi.
 */
@JsonClass(generateAdapter = true)
data class OpenWeatherForecastsResponse(
    @Json(name = "list")
    val forecasts: List<ForecastResponse>,
) {


    /**
     * Cette classe interne représente la prévision météorologique pour une heure donnée.
     * Elle contient des informations telles que l'horaire ( time  ),
     * la température (  temperature  ) et les détails météorologiques ( weather  ).
     */
    @JsonClass(generateAdapter = true)
    data class ForecastResponse(
        @Json(name = "dt")
        val time: Int,
        @Json(name = "main")
        val temperature: TemperatureResponse,
        @Json(name = "weather")
        val weather: List<WeatherResponse>,
    ) {


        /**
         * Cette classe interne de ForecastResponse  représente les données de température
         * pour une prévision. Elle contient la température réelle en Kelvin (temp ).
         */
        @JsonClass(generateAdapter = true)
        data class TemperatureResponse(
            @Json(name = "temp")
            val temp: Double,
        )


        /**
         * Cette classe interne de ForecastResponse  représente les détails météorologiques
         * pour une prévision. Elle contient l'identifiant (  id  ), le titre (  title  )
         * et la description (  description  ) de l'état météorologique.
         */
        @JsonClass(generateAdapter = true)
        data class WeatherResponse(
            @Json(name = "id")
            val id: Int,
            @Json(name = "main")
            val title: String,
            @Json(name = "description")
            val description: String
        )
    }

    fun toDomainModel(): List<WeatherReportModel> {
        return forecasts.map { forecast ->
            val calendar = Calendar.getInstance().apply { timeInMillis = forecast.time * 1000L }


            // Check if the sky is clear (IDs 800 to 802 indicate clear sky conditions)
            val isClearSky = forecast.weather.isNotEmpty() && forecast.weather[0].id in 800..802


            // Get the hour of the date and determine if it's night
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val isNight = hourOfDay < 6 || hourOfDay >= 18


            // Convert temperature to Celsius
            val temperatureCelsius = (forecast.temperature.temp - 273.15).toInt()


            WeatherReportModel(
                isGoodForStargazing = isClearSky && isNight,
                date = calendar,
                temperatureCelsius = temperatureCelsius,
                weatherTitle = forecast.weather[0].title,
                weatherDescription = forecast.weather[0].description
            )
        }
    }
}