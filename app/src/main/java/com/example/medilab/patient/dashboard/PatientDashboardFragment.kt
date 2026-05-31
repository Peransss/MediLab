package com.example.medilab.patient.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.databinding.FragmentPatientDashboardBinding

class PatientDashboardFragment : Fragment() {
    private var _binding: FragmentPatientDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PatientDashboardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPatientDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PatientDashboardViewModel::class.java]

        viewModel.laporanTerbaru.observe(viewLifecycleOwner) { laporan ->
            if (laporan == null) return@observe
            binding.tvPemeriksaan.text = "Pemeriksaan #${laporan.id}"
            binding.tvStatusLaporan.text = "Status: ${laporan.status.uppercase()}"
            val progress = when (laporan.status) {
                "baru" -> 1; "proses" -> 2; "verifikasi" -> 3; "revisi" -> 2; "selesai" -> 4; else -> 0
            }
            binding.progressBar.progress = progress
            binding.progressBar.max = 4
        }

        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
