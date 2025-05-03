package com.example.myapplicationweather342

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // Current weather by coordinates (lat/lon)
    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    // Current weather by ZIP code
    @GET("weather")
    suspend fun getWeatherByZipCode(
        @Query("zip") zip: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
