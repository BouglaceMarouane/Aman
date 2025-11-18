package com.example.aman.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.aman.R
import com.example.aman.viewmodels.WearViewModel
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var tvIntakeAmount: TextView
    private lateinit var tvProgress: TextView
    private lateinit var tvGoal: TextView
    private lateinit var btnAdd200: Button
    private lateinit var btnVoice: ImageButton

    private val viewModel: WearViewModel by viewModels()

    private val speechRecognizer = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            results?.firstOrNull()?.let { text ->
                processVoiceCommand(text)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupObservers()
        setupClickListeners()
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progress_bar)
        tvIntakeAmount = findViewById(R.id.tv_intake_amount)
        tvProgress = findViewById(R.id.tv_progress)
        tvGoal = findViewById(R.id.tv_goal)
        btnAdd200 = findViewById(R.id.btn_add_200)
        btnVoice = findViewById(R.id.btn_voice)
    }

    private fun setupObservers() {
        viewModel.todayIntake.observe(this) { intake ->
            tvIntakeAmount.text = "$intake ml"
        }

        viewModel.dailyGoal.observe(this) { goal ->
            val intake = viewModel.todayIntake.value ?: 0
            val progress = if (goal > 0) {
                ((intake.toFloat() / goal) * 100).toInt()
            } else {
                0
            }
            progressBar.progress = progress
            tvProgress.text = "$progress%"
            tvGoal.text = "Goal: $goal ml"
        }
    }

    private fun setupClickListeners() {
        btnAdd200.setOnClickListener {
            viewModel.addWater(200)
            Toast.makeText(this, "+200ml", Toast.LENGTH_SHORT).show()
        }

        btnVoice.setOnClickListener {
            startVoiceRecognition()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_prompt))
        }

        try {
            speechRecognizer.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.voice_not_available, Toast.LENGTH_SHORT).show()
        }
    }

    private fun processVoiceCommand(text: String) {
        // Extract number from voice input
        val amount = text.replace(Regex("[^0-9]"), "").toIntOrNull()

        if (amount != null && amount > 0 && amount <= 1000) {
            viewModel.addWater(amount)
            Toast.makeText(this, "+${amount}ml", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.voice_not_recognized, Toast.LENGTH_SHORT).show()
        }
    }
}