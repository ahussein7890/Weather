package com.example.myapplicationweather342

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(viewModel: WeatherViewModel) {
    val forecastList = viewModel.forecastData.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("5-Day Forecast") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(forecastList) { item ->
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = item.dateText, fontSize = 16.sp)
                    Text(text = "${item.main.temp}°C", fontSize = 20.sp)
                    Text(text = item.weather[0].description, fontSize = 14.sp)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}
