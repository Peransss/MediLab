package com.example.medilab.patient.hasil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.adapter.LaporanAdapter
import com.example.medilab.databinding.FragmentHasilBinding

class HasilFragment : Fragment() {
    private var _binding: FragmentHasilBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HasilViewModel
    private lateinit var adapter: LaporanAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHasilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HasilViewModel::class.java]

        adapter = LaporanAdapter { }
        binding.rvHasil.adapter = adapter

        viewModel.laporanSelesai.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
