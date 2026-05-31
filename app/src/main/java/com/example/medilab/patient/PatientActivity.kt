package com.example.medilab.patient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.medilab.databinding.ActivityPatientBinding

class PatientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPatientBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
