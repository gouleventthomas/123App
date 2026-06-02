package com.pb123.hub

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.pb123.hub.guides.chrono.ChronoScheduler

class HubApp : Application() {
    override fun onCreate() {
        super.onCreate()
        createDryingChannel()
    }

    private fun createDryingChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ChronoScheduler.CHANNEL_ID,
                "Séchage colle",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Alerte quand le véhicule peut rouler après la pose."
                enableVibration(true)
                enableLights(true)
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}
