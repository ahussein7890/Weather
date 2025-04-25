package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationWeatherService : LifecycleService() {

    companion object {
        private const val CHANNEL_ID = "location_weather_channel"
        private const val NOTIF_ID   = 1001
    }

    private lateinit var fusedClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        // Kick off as a foreground service
        startForeground(
            NOTIF_ID,
            buildNotification(
                title = getString(R.string.loading_weather),
                temp  = "",
                desc  = ""
            )
        )

        requestLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val req = LocationRequest.create().apply {
            interval         = 10 * 60_000L
            fastestInterval  =  5 * 60_000L
            priority         = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Must check BOTH fine and coarse if you need fine
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) return

        fusedClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper())
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let {
                fetchAndNotify(it.latitude, it.longitude)
            }
        }
    }

    private fun fetchAndNotify(lat: Double, lon: Double) {
        lifecycleScope.launch {
            try {
                val weather = WeatherApiClient
                    .apiService
                    .getWeatherByCoordinates(
                        lat, lon,
                        BuildConfig.OPEN_WEATHER_API_KEY
                    )

                withContext(Dispatchers.Main) {
                    updateNotification(weather)
                }
            } catch (_: Exception) {
                // ignore failures to keep service alive
            }
        }
    }

    @SuppressLint("MissingPermission") // we've already checked at launch
    private fun updateNotification(w: WeatherResponse) {
        NotificationManagerCompat.from(this).notify(
            NOTIF_ID,
            buildNotification(
                title = w.cityName,
                temp  = "${w.main.temperature}°",
                desc  = w.weather.first().description
            )
        )
    }

    private fun buildNotification(
        title: String,
        temp: String,
        desc: String
    ): Notification {
        val intent = Intent(this, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pi = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        PendingIntent.FLAG_IMMUTABLE else 0
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(listOf(temp, desc).filter(String::isNotBlank).joinToString(", "))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
            }
            (getSystemService(NotificationManager::class.java))
                ?.createNotificationChannel(chan)
        }
    }

    override fun onDestroy() {
        fusedClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }
}
