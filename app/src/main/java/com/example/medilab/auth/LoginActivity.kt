package com.example.medilab.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.medilab.databinding.ActivityLoginBinding
import com.example.medilab.patient.PatientActivity
import com.example.medilab.staff.StaffActivity
import com.example.medilab.util.Constants
import com.example.medilab.util.Validators

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.loginResult.observe(this) { (success, msg) ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        authViewModel.passwordResetResult.observe(this) { (success, msg) ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        authViewModel.loginRole.observe(this) { role ->
            navigateToRole(role)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (!Validators.isValidEmail(email)) {
                Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Validators.isValidPassword(password)) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.login(email, password)
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterPatientActivity::class.java))
        }

        binding.tvLupaPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Masukkan email terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.sendPasswordReset(email)
        }
    }

    private fun navigateToRole(role: String) {
        when (role) {
            Constants.ROLE_ADMIN, Constants.ROLE_PETUGAS -> {
                startActivity(Intent(this, StaffActivity::class.java))
            }
            Constants.ROLE_DOKTER -> {
                startActivity(Intent(this, StaffActivity::class.java))
            }
            Constants.ROLE_PASIEN -> {
                startActivity(Intent(this, PatientActivity::class.java))
            }
        }
        finish()
    }
}
