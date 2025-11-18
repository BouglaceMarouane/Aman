package com.example.aman.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aman.data.dao.DailyTotal
import com.example.aman.data.repository.AmanRepository
import com.example.aman.utils.DateUtils
import com.example.aman.utils.PreferenceManager
import com.example.aman.utils.WaterDataSyncManager
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AmanRepository(application)
    private val preferenceManager = PreferenceManager(application)
    private val syncManager = WaterDataSyncManager(application)

    val todayTotal: LiveData<Int?> = repository.waterRepository.getTodayTotal()

    private val _dailyGoal = MutableLiveData<Int>()
    val dailyGoal: LiveData<Int> = _dailyGoal

    private val _weeklyData = MutableLiveData<List<Pair<String, Int>>>()
    val weeklyData: LiveData<List<Pair<String, Int>>> = _weeklyData

    var goalReachedToday = false

    init {
        _dailyGoal.value = preferenceManager.dailyGoal

        // Check if goal was already reached today
        todayTotal.observeForever { total ->
            val goal = _dailyGoal.value ?: 2000
            if ((total ?: 0) >= goal) {
                val today = DateUtils.getStartOfDay()
                val lastReachedDate = preferenceManager.userId?.let {
                    // You can track this in preferences if needed
                    0L
                }
                goalReachedToday = DateUtils.isToday(lastReachedDate ?: 0L)
            }
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.waterRepository.addWaterIntake(amountMl)

            // Sync with Wear OS
            val total = todayTotal.value ?: 0
            val goal = _dailyGoal.value ?: 2000
            syncManager.syncWaterData(total + amountMl, goal)
        }
    }

    suspend fun loadWeeklyData() {
        viewModelScope.launch {
            val data = repository.waterRepository.getLastSevenDays()

            // Convert to display format
            val formattedData = data.map { dailyTotal ->
                val date = dailyTotal.date
                val dayOfWeek = date.substring(8, 10) // Get day number
                dayOfWeek to dailyTotal.total
            }.reversed() // Show oldest to newest

            _weeklyData.value = formattedData
        }
    }
}