package com.example.myapplicationweather342

import io.mockk.coEvery
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel
    private val fakeResponse = WeatherResponse(
        cityName = "St. Paul",
        coord = Coord(lat = 44.9537, lon = -93.0900),
        // Add other required fields if your WeatherResponse requires them
    )

    @Before
    fun setup() {
        viewModel = WeatherViewModel()
        mockkObject(WeatherApiClient.apiService)
    }

    @Test
    fun `fetchWeatherByCoordinates should update weatherData with response`() = runTest {
        // Mock success
        coEvery {
            WeatherApiClient.apiService.getWeatherByCoordinates(any(), any(), any())
        } returns fakeResponse

        viewModel.fetchWeatherByCoordinates(44.9537, -93.0900, "test_api_key")

        // Delay not needed with runTest
        assertEquals("St. Paul", viewModel.weatherData.value?.cityName)
    }

    @Test
    fun `fetchWeatherByCoordinates should handle exception and set weatherData to null`() = runTest {
        // Mock error
        coEvery {
            WeatherApiClient.apiService.getWeatherByCoordinates(any(), any(), any())
        } throws Exception("Network error")

        viewModel.fetchWeatherByCoordinates(0.0, 0.0, "bad_key")

        assertNull(viewModel.weatherData.value)
    }
}


