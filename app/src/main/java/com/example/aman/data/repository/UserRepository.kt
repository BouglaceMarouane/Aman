package com.example.aman.data.repository

import androidx.lifecycle.LiveData
import com.example.aman.data.dao.UserProfileDao
import com.example.aman.data.entities.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val userProfileDao: UserProfileDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    // ✅ EXISTING METHODS (Keep these)

    fun getUserProfile(uid: String): LiveData<UserProfile?> {
        return userProfileDao.getUserProfile(uid)
    }

    suspend fun getUserProfileOnce(uid: String): UserProfile? {
        return userProfileDao.getUserProfileOnce(uid)
    }

    suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.update(userProfile)
        // ✅ ALSO sync to Firestore
        syncToFirestore(userProfile)
    }

    suspend fun deleteUserProfile(uid: String) {
        userProfileDao.delete(uid)
        // ✅ ALSO delete from Firestore
        deleteFromFirestore(uid)
    }

    suspend fun deleteAll() {
        userProfileDao.deleteAll()
    }

    // ✅ NEW METHODS FOR FIRESTORE SYNC

    /**
     * Insert user profile and sync to Firestore
     */
    suspend fun insertUserProfile(userProfile: UserProfile) {
        // Save to Room (local)
        userProfileDao.insert(userProfile)

        // Sync to Firestore (cloud)
        syncToFirestore(userProfile)
    }

    /**
     * Save or update user profile in both Room and Firestore
     */
    suspend fun saveUserProfile(userProfile: UserProfile) {
        // Check if user exists
        val existingUser = getUserProfileOnce(userProfile.uid)

        if (existingUser != null) {
            // Update existing
            updateUserProfile(userProfile)
        } else {
            // Insert new
            insertUserProfile(userProfile)
        }
    }

    /**
     * Sync user profile from Firestore to Room
     */
    suspend fun syncFromFirestore(uid: String): UserProfile? {
        return try {
            val document = usersCollection.document(uid).get().await()
            document.toObject(UserProfile::class.java)?.also { profile ->
                userProfileDao.insert(profile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Sync user profile to Firestore
     */
    private suspend fun syncToFirestore(userProfile: UserProfile) {
        try {
            usersCollection.document(userProfile.uid)
                .set(userProfile)
                .await()
        } catch (e: Exception) {
            // Log error but don't crash - local save is done
            e.printStackTrace()
        }
    }

    /**
     * Delete user profile from Firestore
     */
    private suspend fun deleteFromFirestore(uid: String) {
        try {
            usersCollection.document(uid)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Check if user profile needs birthday update
     */
    suspend fun needsBirthdayUpdate(uid: String): Boolean {
        val profile = getUserProfileOnce(uid)
        return profile?.birthDate == 0L
    }
}