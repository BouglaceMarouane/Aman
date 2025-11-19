package com.example.aman.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.aman.R
import com.example.aman.databinding.ActivityOnboardingBinding
import com.example.aman.ui.adapters.OnboardingAdapter
import com.example.aman.utils.PreferenceManager

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        setupStatusBar()
        setupViewPager()
        setupButtons()
        setupIndicator()
    }

    private fun setupStatusBar() {
        // Set status bar to transparent with dark icons
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = true
    }

    private fun setupViewPager() {
        adapter = OnboardingAdapter()
        binding.viewPager.adapter = adapter

        // Update button text and indicator based on current page
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.customIndicator.setCurrentPosition(position)

                if (position == adapter.itemCount - 1) {
                    binding.btnNext.text = getString(R.string.get_started)
                } else {
                    binding.btnNext.text = getString(R.string.next)
                }
            }
        })
    }

    private fun setupIndicator() {
        binding.customIndicator.setDotCount(adapter.itemCount)
        binding.customIndicator.setCurrentPosition(0)
    }

    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem

            if (currentItem < adapter.itemCount - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        preferenceManager.isOnboardingCompleted = true

        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}