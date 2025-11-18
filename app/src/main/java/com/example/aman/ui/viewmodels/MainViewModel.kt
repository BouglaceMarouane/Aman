package com.example.aman.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aman.data.entities.UserProfile
import com.example.aman.data.repository.AmanRepository
import com.example.aman.utils.PreferenceManager
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AmanRepository(application)
    private val preferenceManager = PreferenceManager(application)

    val userProfile: LiveData<UserProfile?>
    val todayTotal: LiveData<Int?>

    private val _dailyGoal = MutableLiveData<Int>()
    val dailyGoal: LiveData<Int> = _dailyGoal

    init {
        val userId = preferenceManager.userId ?: "test_user"
        userProfile = repository.userRepository.getUserProfile(userId)
        todayTotal = repository.waterRepository.getTodayTotal()
        _dailyGoal.value = preferenceManager.dailyGoal
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.waterRepository.addWaterIntake(amountMl)
        }
    }

    fun updateDailyGoal(goalMl: Int) {
        preferenceManager.dailyGoal = goalMl
        _dailyGoal.value = goalMl

        viewModelScope.launch {
            val userId = preferenceManager.userId
            if (userId != null) {
                repository.userRepository.getUserProfileOnce(userId)?.let { profile ->
                    val updatedProfile = profile.copy(dailyGoalMl = goalMl)
                    repository.userRepository.updateUserProfile(updatedProfile)
                }
            }
        }
    }
}