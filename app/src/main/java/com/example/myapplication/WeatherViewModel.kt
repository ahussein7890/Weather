package com.example.myapplicationweather342

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    // Current weather data
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    // 5-day forecast data (one entry per calendar day)
    private val _forecastData = MutableStateFlow<List<ForecastItem>>(emptyList())
    val forecastData: StateFlow<List<ForecastItem>> = _forecastData

    fun fetchWeatherByCoordinates(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = WeatherApiClient.apiService
                    .getWeatherByCoordinates(lat, lon, apiKey)
                _weatherData.value = response
            } catch (e: Exception) {
                _weatherData.value = null
                Log.e("WeatherVM", "❌ API ERROR (Coordinates): ${e.message}", e)
            }
        }
    }

    fun fetchWeatherByZipCode(zipCode: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = WeatherApiClient.apiService
                    .getWeatherByZipCode(zipCode, apiKey)
                _weatherData.value = response
            } catch (e: Exception) {
                _weatherData.value = null
                Log.e("WeatherVM", "❌ API ERROR (ZIP Code): ${e.message}", e)
            }
        }
    }

    fun fetchForecastByZipCode(zipCode: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = WeatherApiClient.apiService
                    .get5DayForecastByZip(zipCode, apiKey)

                // group by day and take the first 3-hr slot of each calendar date
                val daily = response.list
                    .groupBy { it.dateText.take(10) }  // "YYYY-MM-DD"
                    .map { it.value.first() }

                _forecastData.value = daily
                Log.d("WeatherVM", "✅ Got forecast: ${daily.size} days")
            } catch (e: Exception) {
                _forecastData.value = emptyList()
                Log.e("WeatherVM", "❌ Forecast API ERROR: ${e.message}", e)
            }
        }
    }
}
