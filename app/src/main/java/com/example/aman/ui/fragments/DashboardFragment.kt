package com.example.aman.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.aman.R
import com.example.aman.databinding.FragmentDashboardBinding
import com.example.aman.ui.viewmodels.DashboardViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupButtons()
        setupChart()
    }

    private fun setupObservers() {
        viewModel.todayTotal.observe(viewLifecycleOwner) { total ->
            val amount = total ?: 0
            binding.tvIntakeAmount.text = "$amount ml"

            val goal = viewModel.dailyGoal.value ?: 2000
            updateProgress(amount, goal)
        }

        viewModel.dailyGoal.observe(viewLifecycleOwner) { goal ->
            val amount = viewModel.todayTotal.value ?: 0
            updateProgress(amount, goal)
        }

        viewModel.weeklyData.observe(viewLifecycleOwner) { data ->
            updateChart(data)
        }
    }

    private fun updateProgress(intake: Int, goal: Int) {
        val progress = ((intake.toFloat() / goal) * 100).toInt()
        binding.progressCircular.progress = progress
        binding.tvProgress.text = "$progress%"
        binding.tvGoal.text = "Goal: $goal ml"

// CHECK IF GOAL REACHED
        if (intake >= goal && !viewModel.goalReachedToday) {
            viewModel.goalReachedToday = true
            showGoalReachedToast()  // ✅ USE TOAST INSTEAD
        }
    }

    private fun setupButtons() {
        binding.fabAddWater.setOnClickListener {
            showAddWaterBottomSheet()
        }
    }

    private fun showAddWaterBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_add_water, null)

        val amounts = listOf(50, 100, 200, 250, 500)
        val buttonIds = listOf(
            R.id.btn_50ml,
            R.id.btn_100ml,
            R.id.btn_200ml,
            R.id.btn_250ml,
            R.id.btn_500ml
        )

        amounts.forEachIndexed { index, amount ->
            view.findViewById<View>(buttonIds[index])?.setOnClickListener {
                viewModel.addWater(amount)
                Toast.makeText(context, "+$amount ml", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun setupChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setFitBars(true)
            animateY(1000)

            legend.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
            }

            axisRight.isEnabled = false
        }

        // Load initial data
        lifecycleScope.launch {
            viewModel.loadWeeklyData()
        }
    }

    private fun updateChart(data: List<Pair<String, Int>>) {
        if (data.isEmpty()) return

        val entries = data.mapIndexed { index, pair ->
            BarEntry(index.toFloat(), pair.second.toFloat())
        }

        val dataSet = BarDataSet(entries, "Hydration").apply {
            color = Color.parseColor("#4A90E2")
            valueTextColor = Color.BLACK
            valueTextSize = 10f
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.8f

        binding.barChart.apply {
            this.data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.first })
            xAxis.labelCount = data.size
            invalidate()
        }
    }

    private fun showGoalReachedToast() {
//        binding.lottieAnimation.apply {
//            visibility = View.VISIBLE
//            playAnimation()
//            addAnimatorListener(object : android.animation.Animator.AnimatorListener {
//                override fun onAnimationStart(animation: android.animation.Animator) {}
//                override fun onAnimationEnd(animation: android.animation.Animator) {
//                    visibility = View.GONE
//                }
//                override fun onAnimationCancel(animation: android.animation.Animator) {}
//                override fun onAnimationRepeat(animation: android.animation.Animator) {}
//            })
//        }

        Toast.makeText(context, R.string.goal_reached, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}