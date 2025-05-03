package com.example.myapplicationweather342

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    // ✅ Move API key inside ViewModel
    private val apiKey = "0f61d4ac2507933fd147c5105db3ac8f"

    // ✅ Called by MainActivity to fetch location-based weather
    fun fetchWeatherByLocation() {
        val lat = 44.34
        val lon = 10.99
        fetchWeatherByCoordinates(lat, lon)
    }

    // ✅ Called internally or optionally for zip-based queries later
    fun fetchWeatherByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = WeatherApiClient.apiService.getWeatherByCoordinates(lat, lon, apiKey)
                _weatherData.value = response
            } catch (e: Exception) {
                _weatherData.value = null
                println("❌ API ERROR: ${e.message}")
            }
        }
    }
}
