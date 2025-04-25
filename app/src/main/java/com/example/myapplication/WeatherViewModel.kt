package com.example.myapplication

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log

class WeatherViewModel(app: Application) : AndroidViewModel(app) {

    // inject the key just once
    private val apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY

    // Location client for one‐shot fetch
    private val fusedClient = LocationServices
        .getFusedLocationProviderClient(app)

    // Backing state for current weather
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    // Backing state for 5-day forecast
    private val _forecastData = MutableStateFlow<List<ForecastItem>>(emptyList())
    val forecastData: StateFlow<List<ForecastItem>> = _forecastData

    /** Fetch a single “last known” location, then load weather for it. */
    fun fetchWeatherByLocation() {
        val ctx = getApplication<Application>()
        // check FINE_LOCATION first
        if (ContextCompat.checkSelfPermission(
                ctx, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // no permission → bail
            return
        }

        fusedClient.lastLocation
            .addOnSuccessListener { loc ->
                loc?.let {
                    fetchWeatherByCoordinates(it.latitude, it.longitude)
                }
            }
    }

    /** Load weather for the given coords. */
    fun fetchWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val resp = WeatherApiClient
                    .apiService
                    .getWeatherByCoordinates(lat, lon, apiKey)
                _weatherData.value = resp
            } catch (e: Exception) {
                _weatherData.value = null
                Log.e("WeatherVM", "API ERROR (coords): ${e.message}", e)
            }
        }
    }

    /** Load weather by ZIP. */
    fun fetchWeatherByZipCode(zip: String) {
        viewModelScope.launch {
            try {
                val resp = WeatherApiClient
                    .apiService
                    .getWeatherByZipCode(zip, apiKey)
                _weatherData.value = resp
            } catch (e: Exception) {
                _weatherData.value = null
                Log.e("WeatherVM", "API ERROR (ZIP): ${e.message}", e)
            }
        }
    }

    /** Load 5-day forecast by ZIP. */
    fun fetchForecastByZipCode(zip: String) {
        viewModelScope.launch {
            try {
                val resp = WeatherApiClient
                    .apiService
                    .get5DayForecastByZip(zip, apiKey)

                // group into one entry per day
                val daily = resp.list
                    .groupBy { it.dateText.take(10) }
                    .map { it.value.first() }

                _forecastData.value = daily
            } catch (e: Exception) {
                _forecastData.value = emptyList()
                Log.e("WeatherVM", "Forecast ERROR: ${e.message}", e)
            }
        }
    }
}
