package com.example.a041_stellarforecast.data.repository

import com.example.a041_stellarforecast.data.network.WeatherClient
import com.example.a041_stellarforecast.data.response.OpenWeatherForecastsResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response


class WeatherRepositoryTest {

 /**
  * Dans le contexte des tests unitaires, le terme "cut" est souvent utilisé comme acronyme pour
  * "Class Under Test". Cela fait référence à la classe que vous testez dans un scénario
  * particulier. En d'autres termes, la classe  WeatherRepository  est ici soumise aux tests.
  */
 private lateinit var cut: WeatherRepository //Class Under Test
 private lateinit var dataService: WeatherClient

 @Before
 fun setup() {
  /**
   * La ligne de code   dataService = mockk()   est utilisée pour créer un objet mock de la classe
   * WeatherClient  . Celui-ci imite le comportement de la vraie classe, mais il ne contient pas de
   * logique réelle et cela ne nous dérange pas, car ce n’est pas l’objet qui est soumis aux tests
   * ici !
   */
  dataService = mockk()
  cut = WeatherRepository(dataService)
 }


 @Test
 fun `assert when fetchForecastData is requested then clean data is provided`() = runTest {
  //given: a successful wheather forecast response
  val openForecastResponse = OpenWeatherForecastsResponse(
   listOf(
    OpenWeatherForecastsResponse.ForecastResponse(
     1,
     OpenWeatherForecastsResponse.ForecastResponse.TemperatureResponse(130.0),
     listOf(
      OpenWeatherForecastsResponse.ForecastResponse.WeatherResponse(
       800,
       "title",
       "description"
      )
     )
    )
   )
  )

  coEvery {
   dataService.getWeatherByPosition(any(), any(), any())
  } returns Response.success(openForecastResponse)

  //when: the data is fetched
  val values = run {
   cut.fetchForecastData(0.0, 0.0).toList()
  }

  //then
  /**
   * Dans notre flux, deux états sont émis, d'où la vérification de la taille de la liste, qui doit
   * être égale à deux. Ensuite, nous nous assurons que le premier état dans la liste est bien
   * l'état de chargement, et que le dernier est bien un état de succès contenant notre objet
   * openForecastResponse  transformé en  DomainModel  . Cette approche garantit que le flux suit
   * le scénario attendu, avec une indication claire du chargement suivi d'une réussite dans la
   * récupération des données météorologiques.
   *
   * coVerify { dataService.getForecastByPosition(any(), any(), any()) }   s'assure que la fonction
   * getForecastByPosition  du mock  dataService  a été appelée avec les arguments spécifiés.
   */
  coVerify { dataService.getWeatherByPosition(any(), any(), any()) }
  assertEquals(2, values.size)
  assertEquals(Result.Loading, values[0])
  assertEquals(Result.Success(openForecastResponse.toDomainModel()), values[1])
 }


 @Test
 fun `assert when fetchForecastData fail then result failure is raise`() = runTest {
  //given: an error response from the weather forecast server
  val errorResponseBody = "Error message".toResponseBody("text/plain".toMediaType())
  val response = Response.error<OpenWeatherForecastsResponse>(404, errorResponseBody)
  coEvery {
   dataService.getWeatherByPosition(
    any(),
    any(),
    any()
   )
  } returns response

  //when: the data is fetched
  val values = run {
   cut.fetchForecastData(0.0, 0.0).toList()
  }

  //then
  /**
   * Le test vérifie que la fonction  getForecastByPosition  a été appelée avec les paramètres
   * attendus. Ensuite, il vérifie que le flux résultant contient les états  Loading  et  Failure,
   * indiquant que la récupération des données météorologiques a échoué.
   */
  coVerify { dataService.getWeatherByPosition(any(), any(), any()) }
  assertEquals(2, values.size)
  assertEquals(values[0], Result.Loading)
  assert(values[1] is Result.Failure)
 }
}