package com.example.medilab.patient.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.medilab.auth.LoginActivity
import com.example.medilab.databinding.FragmentPatientProfileBinding

class PatientProfileFragment : Fragment() {
    private var _binding: FragmentPatientProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PatientProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPatientProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PatientProfileViewModel::class.java]

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user == null) return@observe
            binding.tvNama.text = user.nama
            binding.tvId.text = "ID: ${user.id}"
            binding.tvRm.text = "No. RM: ${user.noRekamMedis}"
            binding.tvEmail.text = user.email
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
