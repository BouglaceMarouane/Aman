package com.example.aman.ui.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aman.R
import com.example.aman.databinding.FragmentDashboardBinding
import com.example.aman.ui.adapters.TodayRecordsAdapter
import com.example.aman.ui.viewmodels.DashboardViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var recordsAdapter: TodayRecordsAdapter

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

        setupStatusBar()
        setupDate()
        setupChart()
        setupRecyclerView()
        setupObservers()
        setupFAB()

        // Load data
        lifecycleScope.launch {
            viewModel.loadWeeklyData()
        }
        loadTodayRecords()
    }

    private fun setupStatusBar() {
        // Set status bar to white with dark icons
        activity?.window?.let { window ->
            window.statusBarColor = android.graphics.Color.CYAN
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        }
    }

    private fun setupDate() {
        val dateFormat = SimpleDateFormat("EEEE, d MMM", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(Date())
    }

    private fun setupChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setMaxVisibleValueCount(7)
            setPinchZoom(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelCount = 7
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
            }

            axisRight.isEnabled = false
            legend.isEnabled = true
            animateY(1000)
        }
    }

    private fun setupRecyclerView() {
        recordsAdapter = TodayRecordsAdapter { record ->
            lifecycleScope.launch {
                viewModel.deleteWaterIntake(record.id)
                Toast.makeText(context, "Record deleted", Toast.LENGTH_SHORT).show()

                // ✅ Reload today's records
                loadTodayRecords()

                // ✅ Reload chart to reflect deletion
                viewModel.loadWeeklyData()
            }
        }

        binding.rvTodayRecords.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recordsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.todayTotal.observe(viewLifecycleOwner) { total ->
            val intake = total ?: 0
            val goal = viewModel.dailyGoal.value ?: 2000
            updateProgress(intake, goal)
        }

        viewModel.dailyGoal.observe(viewLifecycleOwner) { goal ->
            val intake = viewModel.todayTotal.value ?: 0
            updateProgress(intake, goal)
        }

        viewModel.weeklyData.observe(viewLifecycleOwner) { weeklyData ->
            updateChart(weeklyData)
        }
    }

    private fun loadTodayRecords() {
        lifecycleScope.launch {
            val records = viewModel.getTodayRecords()
            if (records.isEmpty()) {
                binding.rvTodayRecords.visibility = View.GONE
                binding.tvNoRecords.visibility = View.VISIBLE
            } else {
                binding.rvTodayRecords.visibility = View.VISIBLE
                binding.tvNoRecords.visibility = View.GONE
                recordsAdapter.submitList(records)
            }
        }
    }

    private fun updateProgress(intake: Int, goal: Int) {
        val percentage = if (goal > 0) {
            ((intake.toFloat() / goal) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }

        binding.tvIntakeAmount.text = "$intake/$goal ml"
        binding.tvPercentage.text = "$percentage%"
        animateProgress(percentage)
    }

    private fun animateProgress(targetProgress: Int) {
        val currentProgress = binding.circularProgress.progress
        ObjectAnimator.ofInt(binding.circularProgress, "progress", currentProgress, targetProgress).apply {
            duration = 800
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    private fun updateChart(weeklyData: List<Pair<String, Int>>) {
        if (weeklyData.isEmpty()) {
            binding.barChart.clear()
            binding.barChart.setNoDataText("No data available")
            return
        }

        val entries = weeklyData.mapIndexed { index, (_, amount) ->
            BarEntry(index.toFloat(), amount.toFloat())
        }

        val labels = weeklyData.map { it.first }

        val dataSet = BarDataSet(entries, "Daily Intake (ml)").apply {
            color = resources.getColor(R.color.blue_primary, null)
            valueTextColor = resources.getColor(R.color.blue_primary, null)
            valueTextSize = 10f
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        binding.barChart.apply {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            notifyDataSetChanged() // ✅ Notify chart of data change
            invalidate() // ✅ Force redraw
            animateY(800)
        }
    }

    private fun setupFAB() {
        binding.fabAddWater.setOnClickListener {
            showAddWaterDialog()
        }
    }

    private fun showAddWaterDialog() {
        val amounts = arrayOf("50 ml", "100 ml", "150 ml", "200 ml", "250 ml", "Custom")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Water")
            .setItems(amounts) { _, which ->
                when (which) {
                    0 -> addWater(50)
                    1 -> addWater(100)
                    2 -> addWater(150)
                    3 -> addWater(200)
                    4 -> addWater(250)
                    5 -> showCustomAmountDialog()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCustomAmountDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Enter amount (ml)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(50, 30, 50, 30)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Custom Amount")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val amount = input.text.toString().toIntOrNull()
                if (amount != null && amount > 0 && amount <= 2000) {
                    addWater(amount)
                } else {
                    Toast.makeText(context, "Enter valid amount (1-2000 ml)", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addWater(amountMl: Int) {
        viewModel.addWater(amountMl)
        Toast.makeText(context, "+$amountMl ml added! 💧", Toast.LENGTH_SHORT).show()

        // ✅ Reload chart immediately
        lifecycleScope.launch {
            viewModel.loadWeeklyData()
        }

        // ✅ Reload today's records
        loadTodayRecords()

        checkGoalReached()
    }

    private fun checkGoalReached() {
        val intake = viewModel.todayTotal.value ?: 0
        val goal = viewModel.dailyGoal.value ?: 2000

        if (intake >= goal && !viewModel.goalReachedToday) {
            viewModel.goalReachedToday = true
            Toast.makeText(context, "🎉 Goal reached!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}