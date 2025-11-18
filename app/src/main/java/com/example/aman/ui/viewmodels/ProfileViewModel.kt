package com.example.aman.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.aman.data.entities.UserProfile
import com.example.aman.data.repository.AmanRepository
import com.example.aman.utils.DateUtils
import com.example.aman.utils.PreferenceManager
import com.example.aman.worker.HydrationReminderWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AmanRepository(application)
    private val preferenceManager = PreferenceManager(application)
    private val workManager = WorkManager.getInstance(application)

    val userProfile: LiveData<UserProfile?>

    init {
        val userId = preferenceManager.userId ?: "test_user"
        userProfile = repository.userRepository.getUserProfile(userId)
    }

    fun scheduleReminders() {
        val frequency = preferenceManager.reminderFrequency.toLong()

        val reminderWork = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            frequency, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "hydration_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            reminderWork
        )
    }

    fun cancelReminders() {
        workManager.cancelUniqueWork("hydration_reminder")
    }

    fun rescheduleReminders() {
        cancelReminders()
        scheduleReminders()
    }

    suspend fun exportDataToCSV(): Uri = withContext(Dispatchers.IO) {
        val intakes = repository.waterRepository.getIntakesSince(
            DateUtils.getDaysAgo(30)
        )

        val csvContent = buildString {
            append("Date,Time,Amount (ml)\n")
            intakes.forEach { intake ->
                val date = DateUtils.formatDate(intake.timestamp)
                val time = DateUtils.formatTime(intake.timestamp)
                append("$date,$time,${intake.amountMl}\n")
            }
        }

        val file = File(
            getApplication<Application>().filesDir,
            "hydration_export.csv"
        )
        file.writeText(csvContent)

        FileProvider.getUriForFile(
            getApplication(),
            "${getApplication<Application>().packageName}.fileprovider",
            file
        )
    }
}