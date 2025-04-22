package com.example.a041_stellarforecast

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a041_stellarforecast.databinding.ActivityMainBinding
import com.example.a041_stellarforecast.domain.model.WeatherReportModel
import com.example.a041_stellarforecast.presentation.home.HomeViewModel
import com.example.a041_stellarforecast.presentation.home.WeatherAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.provider.Settings

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), WeatherAdapter.OnItemClickListener {

    override fun onItemClick(item: WeatherReportModel) {
        Toast
            .makeText(
                this,
                "${item.weatherTitle}\nIl fera ${item.temperatureCelsius} °C",
                Toast.LENGTH_SHORT
            )
            .show()
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: HomeViewModel by viewModels()
    private val  customAdapter = WeatherAdapter(this)

    private val MY_PERMISSIONS_REQUEST_LOCATION = 99

    /**
     * Ces variables sont initialisées pour gérer la localisation.  fusedLocationProvider  est une
     * classe qui permet d'obtenir la localisation du téléphone.  locationRequest  est utilisée pour
     * définir des paramètres de demande de localisation, tels que l'intervalle entre les mises à
     * jour, ou la priorité.
     */
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 100000
        priority = LocationRequest.PRIORITY_LOW_POWER
        maxWaitTime = 600000
    }

    /**
     * Un callback est défini pour gérer les résultats de la localisation. Lorsqu'une nouvelle
     * localisation est reçue, la méthode  onLocationResult  est appelée, extrayant la dernière
     * localisation et appelant ensuite la méthode getForecastData du viewModel
     */
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
                viewModel.getForecastData(location.latitude, location.longitude)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * On initialise la variable  fusedLocationProvider  en utilisant la classe
         * LocationServices  pour obtenir une instance de  FusedLocationProviderClient,
         * grâce au contexte de l’application.
         * On appelle la méthode  requestLocationPermission()  pour demander la permission de
         * localisation.
         */
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        defineRecyclerView()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    updateCurrentWeather(it.forecast)
                    binding.progressBar.isVisible = it.isViewLoading
                    if (it.errorMessages?.isNotBlank() == true) {
                        Snackbar.make(binding.root, it.errorMessages, Snackbar.LENGTH_LONG).show()
                    }
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

    /**
     * Dans  onResume  , si la permission de localisation est accordée, on demande des mises à jour
     * de localisation en utilisant  requestLocationUpdates  .
     */
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    /**$
     * Dans  onPause  , on supprime les mises à jour de localisation pour économiser des ressources
     * lorsque l'activité est en arrière-plan, principalement de la batterie.
     */
    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
    }

    /**
     * En utilisant la fonction collect, nous observons les changements d'état.
     * A chaque émission d'une nouvelle liste de données, l'ensemble des données de l'Adapter
     * de la RecyclerView est mis à jour, assurant ainsi une représentation en temps réel des
     * informations dans l'interface utilisateur.
     *
     * Ainsi, cette approche garantit une gestion efficace des changements d'état, une mise à jour
     * réactive de l'interface utilisateur et une expérience utilisateur fluide au sein de votre
     * application Android.
     */
    private fun updateCurrentWeather(forecast: List<WeatherReportModel>){
        customAdapter.submitList(forecast)
    }

    /**
     * Un RecyclerView est un composant d'Android utilisé pour afficher une liste de données
     * dynamique dans une interface utilisateur ; il utilise un Adapter pour lier les données à la
     * vue qui correspondent aux ViewHolders, représentant chaque élément de la liste.
     */
    private fun defineRecyclerView(){
        val layoutManager = LinearLayoutManager(applicationContext /*this*/)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = customAdapter
    }

    /**
     * La fonction  requestLocationPermission()  est utilisée pour demander la permission de
     * localisation à l'utilisateur. Elle utilise la méthode  ActivityCompat.requestPermissions
     * pour afficher la boîte de dialogue standard demandant la permission, que nous avons au
     * préalable définie dans le manifeste.
     */
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    /**
     * La méthode  onRequestPermissionsResult  est appelée lorsque l'utilisateur répond à la demande
     * de permission. Elle vérifie si la réponse correspond à la demande de localisation en fonction
     * du code de demande (dans notre cas MY_PERMISSIONS_REQUEST_LOCATION).
     *
     * Si la permission est accordée (PackageManager.PERMISSION_GRANTED), la méthode demande des
     * mises à jour de localisation en utilisant  fusedLocationProvider?.requestLocationUpdates,
     * comme nous le faisions déjà dans la méthode  onResume.
     *
     * Si la permission n'est pas accordée, aucune action n'est entreprise.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted!
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return
            }
        }
    }
}