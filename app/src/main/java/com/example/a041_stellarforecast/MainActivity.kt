package com.example.a041_stellarforecast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a041_stellarforecast.databinding.ActivityMainBinding
import com.example.a041_stellarforecast.domain.model.WeatherReportModel
import com.example.a041_stellarforecast.presentation.home.HomeViewModel
import com.example.a041_stellarforecast.presentation.home.WeatherAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: HomeViewModel by viewModels()
    private val  customAdapter = WeatherAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        defineRecyclerView()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateCurrentWeather(it.forecast)
                }
            }
        }

        /** Test synchrone instruction*/
//        for (i in 1..10000){
//            Thread.sleep(5)
//        }

        /** Test asynchrone instruction */
//        GlobalScope.launch(Dispatchers.IO) {
//            for (i in 1..10000){
//            Thread.sleep(1)
//            }
//            println("Sleepy function done")
//        }

    }

    private fun updateCurrentWeather(forecast: List<WeatherReportModel>){
        customAdapter.submitList(forecast)
    }

    private fun defineRecyclerView(){
        val layoutManager = LinearLayoutManager(applicationContext /*this*/)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = customAdapter
    }
}