package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("list") val list: List<ForecastItem>
)

data class ForecastItem(
    @SerializedName("dt_txt") val dateText: String,
    @SerializedName("main") val main: ForecastMain,
    @SerializedName("weather") val weather: List<ForecastWeather>
)

data class ForecastMain(
    @SerializedName("temp") val temp: Double
)

data class ForecastWeather(
    @SerializedName("icon") val icon: String,
    @SerializedName("description") val description: String
)
