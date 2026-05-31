package com.example.medilab.staff.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.adapter.LaporanAdapter
import com.example.medilab.databinding.FragmentStaffDashboardBinding

class StaffDashboardFragment : Fragment() {
    private var _binding: FragmentStaffDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: StaffDashboardViewModel
    private lateinit var adapter: LaporanAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStaffDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[StaffDashboardViewModel::class.java]

        adapter = LaporanAdapter { }
        binding.rvPendingVerifikasi.adapter = adapter

        viewModel.laporanBaru.observe(viewLifecycleOwner) { binding.tvBaruCount.text = it.toString() }
        viewModel.laporanProses.observe(viewLifecycleOwner) { binding.tvProsesCount.text = it.toString() }
        viewModel.laporanSelesai.observe(viewLifecycleOwner) { binding.tvSelesaiCount.text = it.toString() }
        viewModel.laporanPendingVerifikasi.observe(viewLifecycleOwner) { adapter.submitList(it) }

        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
