package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherNavGraph(weatherViewModel)
        }
    }
}

// ----------------
// Composables
// ----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherNavGraph(weatherViewModel: WeatherViewModel) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "weather") {
        composable("weather") {
            WeatherScreen(navController, weatherViewModel)
        }
        composable("forecast") {
            // Pass the same navController so ForecastScreen can pop back
            ForecastScreen(
                viewModel     = weatherViewModel,
                navController = navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    navController: NavController,
    weatherViewModel: WeatherViewModel
) {
    val context = LocalContext.current
    val weatherState by weatherViewModel.weatherData.collectAsState()
    var zipCode by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // 1) Launcher for location permission
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // start your foreground service for ongoing notifications
            ContextCompat.startForegroundService(
                context,
                Intent(context, LocationWeatherService::class.java)
            )
            // immediately fetch and display UI weather for current location
            weatherViewModel.fetchWeatherByLocation()
        }
    }

    // 2) Launcher for notifications (Android 13+)
    val notifyLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no‐op */ }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.weather_finder_title),
                        color = Color.Black
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // ZIP code input
            OutlinedTextField(
                value = zipCode,
                onValueChange = {
                    if (it.all(Char::isDigit) && it.length <= 5) zipCode = it
                },
                label = { Text(stringResource(R.string.label_zip_code)) },
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

            Spacer(Modifier.height(8.dp))

            // My Location button
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    // request fine‐location
                    locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    // post notifications if needed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifyLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }) {
                    Icon(
                        Icons.Filled.MyLocation,
                        contentDescription = stringResource(R.string.desc_my_location)
                    )
                }
            }

            // ZIP-based weather & forecast buttons
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (zipCode.length == 5) {
                            weatherViewModel.fetchWeatherByZipCode(zipCode)
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.button_get_weather))
                }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (zipCode.length == 5) {
                            weatherViewModel.fetchForecastByZipCode(zipCode)
                            focusManager.clearFocus()
                            navController.navigate("forecast")
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.button_view_forecast))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Display current weather or loading state
            weatherState?.let { w ->
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(w.cityName, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${w.main.temperature}°",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Feels like ${w.main.temperature}°",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Humidity: ${w.main.humidity}%", fontSize = 16.sp)
                    Text(w.weather.first().description, fontSize = 16.sp)
                }
            } ?: run {
                Spacer(Modifier.weight(1f))
                Text(
                    stringResource(R.string.loading_weather),
                    fontSize = 20.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}
