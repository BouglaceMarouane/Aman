package com.example.aman.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.aman.data.entities.UserProfile

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    fun getUserProfile(uid: String): LiveData<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE uid = :uid")
    suspend fun getUserProfileOnce(uid: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfile)

    @Update
    suspend fun update(userProfile: UserProfile)

    @Query("DELETE FROM user_profile WHERE uid = :uid")
    suspend fun delete(uid: String)

    @Query("DELETE FROM user_profile")
    suspend fun deleteAll()
}