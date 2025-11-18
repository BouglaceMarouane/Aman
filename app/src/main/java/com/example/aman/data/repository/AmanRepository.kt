package com.example.aman.data.repository

import android.content.Context
import com.example.aman.data.db.AmanDatabase

class AmanRepository(context: Context) {
    private val database = AmanDatabase.getDatabase(context)

    val waterRepository = WaterRepository(database.waterIntakeDao())
    val userRepository = UserRepository(database.userProfileDao())
}