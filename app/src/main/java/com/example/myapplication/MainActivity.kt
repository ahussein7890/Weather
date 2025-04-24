package com.example.myapplicationweather342

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            WeatherNavGraph(navController, weatherViewModel)
        }
    }
}

@Composable
fun WeatherNavGraph(navController: NavHostController, weatherViewModel: WeatherViewModel) {
    NavHost(navController = navController, startDestination = "weather") {
        composable("weather") {
            WeatherScreen(navController = navController, weatherViewModel = weatherViewModel)
        }
        composable("forecast") {
            ForecastScreen(viewModel = weatherViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController, weatherViewModel: WeatherViewModel) {
    val weatherState by weatherViewModel.weatherData.collectAsState()
    var zipCode by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Finder", color = Color.Black) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = zipCode,
                onValueChange = { if (it.length <= 5 && it.all { c -> c.isDigit() }) zipCode = it },
                label = { Text("Enter ZIP Code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (zipCode.length == 5) {
                            weatherViewModel.fetchWeatherByZipCode(zipCode, "0f61d4ac2507933fd147c5105db3ac8f")
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get Weather")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (zipCode.length == 5) {
                            weatherViewModel.fetchForecastByZipCode(zipCode, "0f61d4ac2507933fd147c5105db3ac8f")
                            focusManager.clearFocus()
                            navController.navigate("forecast")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Forecast")
                }
            }

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
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Loading weather...",
                    fontSize = 20.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
