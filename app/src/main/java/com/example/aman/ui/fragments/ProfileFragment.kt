package com.example.aman.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.aman.R
import com.example.aman.databinding.FragmentProfileBinding
import com.example.aman.ui.activities.AuthActivity
import com.example.aman.ui.viewmodels.ProfileViewModel
import com.example.aman.utils.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        preferenceManager = PreferenceManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupSwitches()
        setupButtons()
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.tvUserName.text = it.displayName
                binding.tvUserEmail.text = it.email
                binding.tvDailyGoal.text = "${it.dailyGoalMl} ml"
            }
        }
    }

    private fun setupSwitches() {
        binding.switchNotifications.isChecked = preferenceManager.notificationsEnabled

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            preferenceManager.notificationsEnabled = isChecked
            if (isChecked) {
                viewModel.scheduleReminders()
            } else {
                viewModel.cancelReminders()
            }
        }
    }

    private fun setupButtons() {
        binding.btnExportData.setOnClickListener {
            exportData()
        }

        binding.btnLanguage.setOnClickListener {
            showLanguageDialog()
        }

        binding.btnReminderFrequency.setOnClickListener {
            showReminderFrequencyDialog()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun exportData() {
        lifecycleScope.launch {
            try {
                val uri = viewModel.exportDataToCSV()

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                startActivity(Intent.createChooser(shareIntent, getString(R.string.export_data)))
            } catch (e: Exception) {
                Toast.makeText(context, R.string.export_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Français", "English", "العربية")
        val languageCodes = arrayOf("fr", "en", "ar")
        val currentLanguage = preferenceManager.language
        val currentIndex = languageCodes.indexOf(currentLanguage)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_language)
            .setSingleChoiceItems(languages, currentIndex) { dialog, which ->
                preferenceManager.language = languageCodes[which]
                Toast.makeText(context, R.string.language_changed, Toast.LENGTH_SHORT).show()
                dialog.dismiss()

                // Restart activity to apply language
                activity?.recreate()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showReminderFrequencyDialog() {
        val frequencies = arrayOf("Every 1 hour", "Every 2 hours", "Every 3 hours", "Every 4 hours")
        val frequencyValues = arrayOf(1, 2, 3, 4)
        val currentFrequency = preferenceManager.reminderFrequency
        val currentIndex = frequencyValues.indexOf(currentFrequency)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.reminder_frequency)
            .setSingleChoiceItems(frequencies, currentIndex) { dialog, which ->
                preferenceManager.reminderFrequency = frequencyValues[which]
                viewModel.rescheduleReminders()
                Toast.makeText(context, R.string.frequency_updated, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.logout)
            .setMessage(R.string.logout_confirmation)
            .setPositiveButton(R.string.yes) { _, _ ->
                FirebaseAuth.getInstance().signOut()
                preferenceManager.clear()

                val intent = Intent(requireContext(), AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}