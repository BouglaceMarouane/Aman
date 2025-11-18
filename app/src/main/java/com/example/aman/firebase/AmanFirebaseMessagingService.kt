package com.example.aman.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.aman.R
import com.example.aman.ui.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AmanFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle FCM messages
        remoteMessage.notification?.let { notification ->
            showNotification(
                notification.title ?: "Aman",
                notification.body ?: ""
            )
        }

        // Handle data payload
        remoteMessage.data.isNotEmpty().let {
            val type = remoteMessage.data["type"]
            when (type) {
                "motivation" -> {
                    val progress = remoteMessage.data["progress"]
                    showMotivationNotification(progress ?: "0")
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send token to your server if needed
        // Log.d("FCM", "New token: $token")
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, "motivation")
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun showMotivationNotification(progress: String) {
        val message = when {
            (progress.toIntOrNull() ?: 0) >= 100 -> getString(R.string.motivation_complete)
            (progress.toIntOrNull() ?: 0) >= 80 -> getString(R.string.motivation_almost)
            (progress.toIntOrNull() ?: 0) >= 50 -> getString(R.string.motivation_halfway)
            else -> getString(R.string.motivation_start)
        }

        showNotification(getString(R.string.motivation_title), message)
    }
}