package com.example.aman.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.aman.data.entities.WaterIntake

@Dao
interface WaterIntakeDao {

    @Insert
    suspend fun insert(waterIntake: WaterIntake): Long

    @Query("SELECT * FROM water_intake ORDER BY timestamp DESC")
    fun getAllIntakes(): LiveData<List<WaterIntake>>

    @Query("SELECT SUM(amountMl) FROM water_intake WHERE timestamp >= :startOfDay AND timestamp < :endOfDay")
    fun getTodayTotal(startOfDay: Long, endOfDay: Long): LiveData<Int?>

    @Query("""
        SELECT 
            DATE(timestamp/1000, 'unixepoch') as date,
            SUM(amountMl) as total
        FROM water_intake 
        WHERE timestamp >= :startTime 
        GROUP BY date 
        ORDER BY date DESC 
        LIMIT 7
    """)
    suspend fun getLastSevenDays(startTime: Long): List<DailyTotal>

    @Query("SELECT * FROM water_intake WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    suspend fun getIntakesSince(startTime: Long): List<WaterIntake>

    @Query("SELECT * FROM water_intake WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    suspend fun getIntakesBetween(start: Long, end: Long): List<WaterIntake>

    @Query("DELETE FROM water_intake WHERE id = :id")
    suspend fun deleteById(id: Int)
    @Delete
    suspend fun delete(waterIntake: WaterIntake)

    @Query("DELETE FROM water_intake")
    suspend fun deleteAll()
}

data class DailyTotal(
    val date: String,
    val total: Int
)