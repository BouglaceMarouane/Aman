package com.example.aman.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aman.data.db.AmanDatabase
import com.example.aman.data.entities.UserProfile
import com.example.aman.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val userRepository: UserRepository

    init {
        val database = AmanDatabase.getDatabase(application)
        userRepository = UserRepository(database.userProfileDao())
    }

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val userId: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                val result = auth.signInWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid

                if (userId != null) {
                    // Try to sync user profile from Firestore
                    userRepository.syncFromFirestore(userId)
                    _authState.value = AuthState.Success(userId)
                } else {
                    _authState.value = AuthState.Error("Login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String, name: String, birthday: Long) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading

                // Create Firebase Auth account
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = result.user?.uid

                if (userId != null) {
                    // Update Firebase Auth profile with name
                    result.user?.updateProfile(
                        com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                    )?.await()

                    // ✅ Create UserProfile with birthday
                    val userProfile = UserProfile(
                        uid = userId,
                        displayName = name,
                        email = email,
                        dailyGoalMl = 2000,
                        birthDate = birthday,  // ✅ Birthday saved here!
                        locale = "en"
                    )

                    // ✅ Save to Room and Firestore
                    userRepository.saveUserProfile(userProfile)

                    _authState.value = AuthState.Success(userId)
                } else {
                    _authState.value = AuthState.Error("Registration failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    /**
     * Save Google Sign-In user
     */
    fun saveGoogleSignInUser(userId: String, name: String, email: String) {
        viewModelScope.launch {
            try {
                // Check if user already exists
                var userProfile = userRepository.getUserProfileOnce(userId)

                if (userProfile == null) {
                    // New Google user - create profile WITHOUT birthday
                    userProfile = UserProfile(
                        uid = userId,
                        displayName = name,
                        email = email,
                        dailyGoalMl = 2000,
                        birthDate = 0, // ⚠️ Will be set later
                        locale = "en"
                    )
                    userRepository.saveUserProfile(userProfile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Password reset failed")
            }
        }
    }
}