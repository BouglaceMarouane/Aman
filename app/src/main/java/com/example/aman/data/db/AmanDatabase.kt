package com.example.aman.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aman.data.dao.UserProfileDao
import com.example.aman.data.dao.WaterIntakeDao
import com.example.aman.data.entities.UserProfile
import com.example.aman.data.entities.WaterIntake

@Database(
    entities = [WaterIntake::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
abstract class AmanDatabase : RoomDatabase() {

    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AmanDatabase? = null

        fun getDatabase(context: Context): AmanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AmanDatabase::class.java,
                    "aman_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}