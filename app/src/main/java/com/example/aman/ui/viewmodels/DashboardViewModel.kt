package com.example.aman.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aman.data.entities.WaterIntake
import com.example.aman.data.repository.AmanRepository
import com.example.aman.utils.DateUtils
import com.example.aman.utils.PreferenceManager
import com.example.aman.utils.WaterDataSyncManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

            // ✅ FIXED: Only show days with actual usage
            val formattedData = data
                .filter { it.total > 0 } // Only days with water intake
                .map { dailyTotal ->
                    val date = dailyTotal.date
                    // Get day number (18, 19, etc.)
                    val dayOfWeek = date.substring(8, 10)
                    dayOfWeek to dailyTotal.total
                }
                .reversed() // Show oldest to newest

            _weeklyData.value = formattedData
        }
    }

    // ✅ Get today's records
    suspend fun getTodayRecords(): List<WaterIntake> {
        return repository.waterRepository.getTodayRecords()
    }

    // ✅ Delete water intake record
    suspend fun deleteWaterIntake(id: Int) {
        repository.waterRepository.deleteWaterIntake(id)
    }
}