package com.example.aman.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.aman.R
import com.example.aman.receivers.HydrationReminderReceiver
import com.example.aman.ui.activities.MainActivity
import com.example.aman.utils.PreferenceManager

class HydrationReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val preferenceManager = PreferenceManager(applicationContext)

        // Check if notifications are enabled
        if (!preferenceManager.notificationsEnabled) {
            return Result.success()
        }

        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Action to add water directly from notification
        val addWaterIntent = Intent(applicationContext, HydrationReminderReceiver::class.java).apply {
            action = "com.example.aman.ACTION_ADD_WATER"
            putExtra("amount", 200)
        }

        val addWaterPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1,
            addWaterIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "hydration_reminders")
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(applicationContext.getString(R.string.reminder_title))
            .setContentText(applicationContext.getString(R.string.reminder_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_add,
                applicationContext.getString(R.string.add_200ml),
                addWaterPendingIntent
            )
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}