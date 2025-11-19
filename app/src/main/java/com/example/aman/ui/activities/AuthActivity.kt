package com.example.aman.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.example.aman.R
import com.example.aman.databinding.ActivityAuthBinding
import com.example.aman.ui.viewmodels.AuthViewModel
import com.example.aman.utils.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    private var isSignUpMode = false
    private var selectedBirthday: Long = 0

    // Triple-click auth skip
    private var clickCount = 0
    private var lastClickTime = 0L
    private val clickTimeWindow = 1000L

    // Google Sign-In launcher
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                firebaseAuthWithGoogle(token)
            } ?: run {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign-in failed", e)
            Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "AuthActivity"
        private const val MIN_AGE_YEARS = 18
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)
        firebaseAuth = FirebaseAuth.getInstance()

        setupStatusBar()
        setupGoogleSignIn()
        setupObservers()
        setupButtons()
        updateUIMode()
    }

    private fun setupStatusBar() {
        // Set status bar to transparent with dark icons
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = true
    }

    @Suppress("DEPRECATION")
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupObservers() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnAction.isEnabled = false
                    binding.btnToggleMode.isEnabled = false
                    binding.btnGoogleSignin.isEnabled = false
                }
                is AuthViewModel.AuthState.Success -> {
                    preferenceManager.userId = state.userId
                    navigateToDashboard()
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAction.isEnabled = true
                    binding.btnToggleMode.isEnabled = true
                    binding.btnGoogleSignin.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is AuthViewModel.AuthState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAction.isEnabled = true
                    binding.btnToggleMode.isEnabled = true
                    binding.btnGoogleSignin.isEnabled = true
                }
            }
        }
    }

    private fun setupButtons() {
        // Email/Password Action Button
        binding.btnAction.setOnClickListener {
            handleTripleClick()

            if (isSignUpMode) {
                handleSignUp()
            } else {
                handleSignIn()
            }
        }

        // Google Sign-In Button
        binding.btnGoogleSignin.setOnClickListener {
            signInWithGoogle()
        }

        // Toggle Mode Button
        binding.btnToggleMode.setOnClickListener {
            isSignUpMode = !isSignUpMode
            updateUIMode()
        }

        // Forgot Password
        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                viewModel.resetPassword(email)
                Toast.makeText(this, R.string.password_reset_sent, Toast.LENGTH_SHORT).show()
            } else {
                binding.etEmail.error = getString(R.string.email_required)
            }
        }

        // Birthday Picker
        binding.etBirthday.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        // Set max date to 18 years ago
        val maxDate = Calendar.getInstance().apply {
            add(Calendar.YEAR, -MIN_AGE_YEARS)
        }

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                // Validate age
                if (isAgeValid(selectedDate.timeInMillis)) {
                    selectedBirthday = selectedDate.timeInMillis
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.etBirthday.setText(dateFormat.format(Date(selectedBirthday)))
                    binding.etBirthday.error = null
                } else {
                    binding.etBirthday.error = getString(R.string.must_be_18)
                    selectedBirthday = 0
                }
            },
            maxDate.get(Calendar.YEAR),
            maxDate.get(Calendar.MONTH),
            maxDate.get(Calendar.DAY_OF_MONTH)
        )

        // Set max selectable date to 18 years ago
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis
        datePickerDialog.show()
    }

    private fun isAgeValid(birthDate: Long): Boolean {
        val today = Calendar.getInstance()
        val birth = Calendar.getInstance().apply { timeInMillis = birthDate }

        var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age >= MIN_AGE_YEARS
    }

    private fun updateUIMode() {
        if (isSignUpMode) {
            // SIGN UP MODE
            binding.tvTitle.text = getString(R.string.create_account)
            binding.tilName.visibility = View.VISIBLE
            binding.tilBirthday.visibility = View.VISIBLE
            binding.btnAction.text = getString(R.string.sign_up)
            binding.btnToggleMode.text = getString(R.string.already_have_account)
            binding.tvForgotPassword.visibility = View.GONE
        } else {
            // SIGN IN MODE
            binding.tvTitle.text = getString(R.string.welcome_back)
            binding.tilName.visibility = View.GONE
            binding.tilBirthday.visibility = View.GONE
            binding.btnAction.text = getString(R.string.sign_in)
            binding.btnToggleMode.text = getString(R.string.dont_have_account)
            binding.tvForgotPassword.visibility = View.VISIBLE
        }

        // Clear errors
        binding.etName.error = null
        binding.etEmail.error = null
        binding.etPassword.error = null
        binding.etBirthday.error = null
    }

    private fun handleSignIn() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (validateSignIn(email, password)) {
            viewModel.login(email, password)
        }
    }

    private fun handleSignUp() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (validateSignUp(name, email, password, selectedBirthday)) {
            viewModel.register(email, password, name, selectedBirthday)
        }
    }

    private fun validateSignIn(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.email_required)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.password_required)
            isValid = false
        }

        return isValid
    }

    private fun validateSignUp(
        name: String,
        email: String,
        password: String,
        birthday: Long
    ): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.etName.error = getString(R.string.name_required)
            isValid = false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = getString(R.string.email_required)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = getString(R.string.password_required)
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = getString(R.string.password_too_short)
            isValid = false
        }

        if (birthday == 0L) {
            binding.etBirthday.error = getString(R.string.birthday_required)
            isValid = false
        } else if (!isAgeValid(birthday)) {
            binding.etBirthday.error = getString(R.string.must_be_18)
            isValid = false
        }

        return isValid
    }

    @Suppress("DEPRECATION")
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        binding.progressBar.visibility = View.VISIBLE
        binding.btnGoogleSignin.isEnabled = false

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        // Save user info
                        preferenceManager.userId = it.uid
                        preferenceManager.userName = it.displayName ?: ""
                        preferenceManager.userEmail = it.email ?: ""

                        Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
                        navigateToDashboard()
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.btnGoogleSignin.isEnabled = true
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleTripleClick() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastClickTime < clickTimeWindow) {
            clickCount++
            if (clickCount >= 2) {
                skipAuth()
                clickCount = 0
            }
        } else {
            clickCount = 0
        }

        lastClickTime = currentTime
    }

    private fun skipAuth() {
        Toast.makeText(this, "Auth skipped for testing", Toast.LENGTH_SHORT).show()
        preferenceManager.userId = "test_user_${System.currentTimeMillis()}"
        navigateToDashboard()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}