package com.example.aman.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val uid: String,
    val displayName: String,
    val email: String = "",
    val dailyGoalMl: Int = 2000,
    val birthDate: Long,
    val locale: String = "fr"
)