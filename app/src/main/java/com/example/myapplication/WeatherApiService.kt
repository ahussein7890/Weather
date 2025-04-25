package com.example.myapplication

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // Current weather by coordinates (Fahrenheit)
    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial"   // °F
    ): WeatherResponse

    // Current weather by ZIP code (Fahrenheit)
    @GET("weather")
    suspend fun getWeatherByZipCode(
        @Query("zip") zip: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial"   // °F
    ): WeatherResponse

    // Free 5-day / 3-hour forecast by ZIP code (Fahrenheit)
    @GET("forecast")
    suspend fun get5DayForecastByZip(
        @Query("zip") zip: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "imperial"   // °F
    ): ForecastResponse
}
