package com.example.a041_stellarforecast.data.network

import com.example.a041_stellarforecast.data.response.OpenWeatherForecastsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *     Cette interface déclare une méthode appelée  getWeatherByPosition  .
 *     Le mot-clé suspend indique que cette méthode peut être suspendue, ce qui signifie qu'elle
 *     peut être utilisée de manière asynchrone dans des fonctions coroutine, une notion que vous
 *     pourrez explorer plus tard dans ce cours.
 *
 *     Le type de retour est Response<OpenWeatherResponse> : La fonction renvoie un objet Response
 *     de la bibliothèque Retrofit. Cette enveloppe contient la réponse HTTP du serveur, ainsi que
 *     le corps de la réponse en utilisant le modèle défini par OpenWeatherResponse.
 *
 *     L'annotation GET indique que la méthode getWeatherByPosition effectuera une requête HTTP GET
 *     à l'URL spécifié, ici "/data/2.5/forecast". L'URL complète sera construite en fonction de la
 *     base de l'URL définie dans la configuration Retrofit
 */
interface WeatherClient {

    @GET("/data/2.5/forecast")
    suspend fun getWeatherByPosition(
        @Query(value = "lat") latitude: Double,
        @Query(value = "lon") longitude: Double,
        @Query(value = "appid") apiKey: String
    ): Response<OpenWeatherForecastsResponse>

}