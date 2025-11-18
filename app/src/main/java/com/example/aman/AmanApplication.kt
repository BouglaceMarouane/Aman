package com.example.aman

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp

class AmanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Create notification channels
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                "hydration_reminders",
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to drink water"
                enableVibration(true)
                enableLights(true)
            }

            val motivationChannel = NotificationChannel(
                "motivation",
                "Motivational Messages",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Motivational push notifications"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(reminderChannel)
            notificationManager?.createNotificationChannel(motivationChannel)
        }
    }
}