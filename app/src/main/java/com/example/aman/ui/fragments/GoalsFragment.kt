package com.example.aman.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.aman.R
import com.example.aman.databinding.FragmentGoalsBinding
import com.example.aman.ui.viewmodels.GoalsViewModel
import com.google.android.material.slider.Slider

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupSlider()
        setupButtons()
    }

    private fun setupObservers() {
        viewModel.dailyGoal.observe(viewLifecycleOwner) { goal ->
            binding.tvGoalAmount.text = "$goal ml"
            binding.slider.value = goal.toFloat()
        }

        viewModel.todayTotal.observe(viewLifecycleOwner) { total ->
            val amount = total ?: 0
            val goal = viewModel.dailyGoal.value ?: 2000
            val progress = ((amount.toFloat() / goal) * 100).toInt()

            binding.tvTodayProgress.text = "$amount ml / $goal ml"
            binding.progressBar.progress = progress
            binding.tvProgressPercentage.text = "$progress%"
        }
    }

    private fun setupSlider() {
        binding.slider.apply {
            valueFrom = 1000f
            valueTo = 5000f
            stepSize = 100f

            addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {}

                override fun onStopTrackingTouch(slider: Slider) {
                    viewModel.updateDailyGoal(slider.value.toInt())
                }
            })
        }
    }

    private fun setupButtons() {
        binding.btnSaveGoal.setOnClickListener {
            val goal = binding.slider.value.toInt()
            viewModel.updateDailyGoal(goal)
            Toast.makeText(context, R.string.goal_saved, Toast.LENGTH_SHORT).show()
        }

        binding.btnResetGoal.setOnClickListener {
            viewModel.resetToDefaultGoal()
            Toast.makeText(context, R.string.goal_reset, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}