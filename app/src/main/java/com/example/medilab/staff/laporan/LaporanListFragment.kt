package com.example.medilab.staff.laporan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.medilab.R
import com.example.medilab.adapter.LaporanAdapter
import com.example.medilab.databinding.FragmentLaporanListBinding

class LaporanListFragment : Fragment() {
    private var _binding: FragmentLaporanListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LaporanViewModel
    private lateinit var adapter: LaporanAdapter

    private val statusTabs = listOf("baru", "proses", "verifikasi", "revisi", "selesai", "ditolak")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLaporanListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LaporanViewModel::class.java]

        adapter = LaporanAdapter { laporan ->
            val bundle = Bundle().apply { putString("laporanId", laporan.id) }
            findNavController().navigate(R.id.laporanDetailFragment, bundle)
        }
        binding.rvLaporan.adapter = adapter

        statusTabs.forEach { status ->
            binding.tabStatus.addTab(binding.tabStatus.newTab().setText(status.uppercase()))
        }
        binding.tabStatus.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                viewModel.loadByStatus(statusTabs[tab?.position ?: 0])
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
        viewModel.loadByStatus("baru")
        viewModel.laporanList.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
