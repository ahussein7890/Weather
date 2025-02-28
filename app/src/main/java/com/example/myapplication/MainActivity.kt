package com.example.myapplication // Make sure this matches your actual package name!

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherUI() // Calls the composable function to display the weather screen
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherUI() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Application Title Bar
        TopAppBar(
            title = { Text(text = "Weather Finder", color = Color.Black) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Centralized Weather Information Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "St. Paul, MN",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "72°",
                fontSize = 65.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Feels like 78°",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Display Weather Icon
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = "Weather Condition Icon",
                modifier = Modifier.size(55.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Additional Weather Details
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherDetailRow("Low", "65°")
                WeatherDetailRow("High", "80°")
                WeatherDetailRow("Humidity", "100%")
                WeatherDetailRow("Pressure", "1023 hPa")
            }
        }
    }
}

// Helper Composable for Weather Details
@Composable
fun WeatherDetailRow(label: String, value: String) {
    Text(
        text = "$label: $value",
        fontSize = 16.sp
    )
}