package com.example.aman.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.aman.utils.PreferenceManager
import com.example.aman.worker.HydrationReminderWorker
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule WorkManager tasks after reboot
            val preferenceManager = PreferenceManager(context)

            if (preferenceManager.notificationsEnabled) {
                scheduleReminders(context, preferenceManager)
            }
        }
    }

    private fun scheduleReminders(context: Context, preferenceManager: PreferenceManager) {
        val frequency = preferenceManager.reminderFrequency.toLong()

        val reminderWork = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            frequency, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "hydration_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWork
        )
    }
}