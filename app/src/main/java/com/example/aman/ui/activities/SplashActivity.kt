package com.example.aman.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.example.aman.R
import com.example.aman.utils.PreferenceManager

class SplashActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private val splashDelay = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        preferenceManager = PreferenceManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNext()
        }, splashDelay)

        setupStatusBar()
    }

    private fun setupStatusBar() {
        // Set status bar to transparent with dark icons
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = true
    }

    private fun navigateToNext() {
        val intent = when {
            preferenceManager.isFirstLaunch -> {
                preferenceManager.isFirstLaunch = false
                Intent(this, OnboardingActivity::class.java)
            }
            preferenceManager.userId == null -> {
                Intent(this, AuthActivity::class.java)
            }
            else -> {
                Intent(this, MainActivity::class.java)
            }
        }

        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}