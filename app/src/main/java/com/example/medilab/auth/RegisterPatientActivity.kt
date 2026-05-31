package com.example.medilab.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.medilab.databinding.ActivityRegisterPatientBinding
import com.example.medilab.util.Validators

class RegisterPatientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPatientBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.registerResult.observe(this) { (success, msg) ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            if (success) finish()
        }

        binding.btnRegister.setOnClickListener {
            val nama = binding.etNama.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val noHP = binding.etNoHP.text.toString().trim()
            val alamat = binding.etAlamat.text.toString().trim()
            val tanggalLahir = binding.etTanggalLahir.text.toString().trim()

            if (!Validators.isNotEmpty(nama, email, password, noHP, alamat, tanggalLahir)) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Validators.isValidEmail(email)) {
                Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Validators.isValidPassword(password)) {
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.registerPatient(email, password, nama, noHP, alamat, tanggalLahir)
        }
    }
}
