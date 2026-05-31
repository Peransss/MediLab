package com.example.medilab.patient.riwayat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.adapter.RiwayatAdapter
import com.example.medilab.databinding.FragmentRiwayatBinding
import com.example.medilab.repository.AuthRepository

class RiwayatFragment : Fragment() {
    private var _binding: FragmentRiwayatBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RiwayatViewModel
    private lateinit var adapter: RiwayatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RiwayatViewModel::class.java]

        adapter = RiwayatAdapter { }
        binding.rvRiwayat.adapter = adapter

        viewModel.riwayatList.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.load(AuthRepository().getCurrentUid())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
