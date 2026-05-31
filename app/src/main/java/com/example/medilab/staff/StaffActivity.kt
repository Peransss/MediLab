package com.example.medilab.staff

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.medilab.databinding.ActivityStaffBinding

class StaffActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStaffBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
