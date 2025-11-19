package com.example.aman.data.repository

import androidx.lifecycle.LiveData
import com.example.aman.data.dao.DailyTotal
import com.example.aman.data.dao.WaterIntakeDao
import com.example.aman.data.entities.WaterIntake
import com.example.aman.utils.DateUtils
import java.util.Calendar

class WaterRepository(private val waterIntakeDao: WaterIntakeDao) {

    suspend fun addWaterIntake(amountMl: Int): Long {
        return waterIntakeDao.insert(WaterIntake(amountMl = amountMl))
    }

    fun getAllIntakes(): LiveData<List<WaterIntake>> {
        return waterIntakeDao.getAllIntakes()
    }

    fun getTodayTotal(): LiveData<Int?> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis

        return waterIntakeDao.getTodayTotal(startOfDay, endOfDay)
    }

    suspend fun getLastSevenDays(): List<DailyTotal> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        return waterIntakeDao.getLastSevenDays(calendar.timeInMillis)
    }

    suspend fun getIntakesSince(timestamp: Long): List<WaterIntake> {
        return waterIntakeDao.getIntakesSince(timestamp)
    }

    suspend fun getTodayRecords(): List<WaterIntake> {
        val startOfDay = DateUtils.getStartOfDay()
        val endOfDay = DateUtils.getEndOfDay()
        return waterIntakeDao.getIntakesBetween(startOfDay, endOfDay)
    }

    /**
     * Delete a water intake record
     */
    suspend fun deleteWaterIntake(id: Int) {
        waterIntakeDao.deleteById(id)
    }

    suspend fun deleteIntake(waterIntake: WaterIntake) {
        waterIntakeDao.delete(waterIntake)
    }

    suspend fun deleteAll() {
        waterIntakeDao.deleteAll()
    }
}