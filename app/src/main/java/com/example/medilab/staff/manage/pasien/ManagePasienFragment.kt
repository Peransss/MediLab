package com.example.medilab.staff.manage.pasien

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.adapter.PasienAdapter
import com.example.medilab.databinding.FragmentManagePasienBinding

class ManagePasienFragment : Fragment() {
    private var _binding: FragmentManagePasienBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ManagePasienViewModel
    private lateinit var adapter: PasienAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManagePasienBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ManagePasienViewModel::class.java]

        adapter = PasienAdapter { user ->
            Toast.makeText(requireContext(), "${user.nama} - ${user.id}", Toast.LENGTH_SHORT).show()
        }
        binding.rvPasien.adapter = adapter

        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        viewModel.pasienList.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
