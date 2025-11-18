package com.example.aman.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aman.data.repository.AmanRepository
import com.example.aman.utils.PreferenceManager
import com.example.aman.utils.WaterDataSyncManager
import kotlinx.coroutines.launch

class GoalsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AmanRepository(application)
    private val preferenceManager = PreferenceManager(application)
    private val syncManager = WaterDataSyncManager(application)

    val todayTotal: LiveData<Int?> = repository.waterRepository.getTodayTotal()

    private val _dailyGoal = MutableLiveData<Int>()
    val dailyGoal: LiveData<Int> = _dailyGoal

    init {
        _dailyGoal.value = preferenceManager.dailyGoal
    }

    fun updateDailyGoal(goalMl: Int) {
        _dailyGoal.value = goalMl
        preferenceManager.dailyGoal = goalMl

        viewModelScope.launch {
            // Update in database
            val userId = preferenceManager.userId
            if (userId != null) {
                val profile = repository.userRepository.getUserProfileOnce(userId)
                profile?.let {
                    val updated = it.copy(dailyGoalMl = goalMl)
                    repository.userRepository.updateUserProfile(updated)
                }
            }

            // Sync with Wear OS
            val total = todayTotal.value ?: 0
            syncManager.syncWaterData(total, goalMl)
        }
    }

    fun resetToDefaultGoal() {
        updateDailyGoal(2000)
    }
}