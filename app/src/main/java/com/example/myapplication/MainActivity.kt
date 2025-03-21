package com.example.myapplicationweather342

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherScreen(weatherViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel = viewModel()) {
    val weatherState by weatherViewModel.weatherData.collectAsState()

    LaunchedEffect(Unit) {
        if (weatherState == null) {
            weatherViewModel.fetchWeatherByCoordinates(44.34, 10.99, "0f61d4ac2507933fd147c5105db3ac8f")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Weather Finder", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray),
        )

        Spacer(modifier = Modifier.height(16.dp))

        weatherState?.let { weather ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(weather.cityName, fontSize = 18.sp, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${weather.main.temperature}°",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text("Feels like ${weather.main.temperature}°", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text("Humidity: ${weather.main.humidity}%", fontSize = 16.sp)
                    Text("Description: ${weather.weather[0].description}", fontSize = 16.sp)
                }
            }
        } ?: run {
            Text(
                text = "Loading weather...",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    }
}








