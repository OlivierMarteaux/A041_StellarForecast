package com.example.a041_stellarforecast.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a041_stellarforecast.data.repository.WeatherRepository
import com.example.a041_stellarforecast.domain.model.WeatherReportModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 *     @HiltViewModel : Cette annotation indique à Hilt que la classe HomeViewModel doit être
 *     injectée automatiquement.
 *
 *     Inject constructor  : Le constructeur de la classe est annoté avec  @Inject  , ce qui permet
 *     à Hilt de fournir automatiquement l'instance de WeatherRepository lorsqu'un objet
 *     HomeViewModel  est créé. Comme toujours, Hilt s’occupe de la création de nos classes pour
 *     nous sans que nous ayons rien à faire de plus.
 *
 *     init : La fonction init est exécutée lors de l’initialisation d'une instance de
 *     HomeViewModel  . Dans ce cas, elle appelle la fonction  getForecastData()  .
 *
 *     getForecastData()  : Cette fonction sert à récupérer les prévisions météorologiques.
 *     Elle utilise une latitude et une longitude prédéfinies pour Paris.
 *
 *     dataRepository.fetchForecastData : Cela appelle la fonction  fetchForecastData
 *     du WeatherRepository, récupérant ainsi les données de manière asynchrone à l'aide de
 *     coroutines.
 *
 *         onEach  : Cette fonction est appelée chaque fois que de nouvelles données sont
 *         émises par la coroutine.
 *
 *         launchIn(viewModelScope) : Cette partie indique que la coroutine doit être lancée dans
 *         le scope du ViewModel. Cela garantit que la coroutine est annulée lorsque le ViewModel
 *         est détruit, évitant les fuites de mémoire.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val dataRepository: WeatherRepository) :
    ViewModel() {

    // Expose screen UI state
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

        init{
            getForecastData()
        }

        private fun getForecastData(){
            // coordonnees maison
            val longitude = 43.521162766326995
            val latitude = 6.8696485006942405
            dataRepository.fetchForecastData(latitude, longitude)
                .onEach{ forecastUpdate ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            forecast = forecastUpdate
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

data class HomeUiState(
    val forecast: List<WeatherReportModel> = emptyList(),
)