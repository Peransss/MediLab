package com.example.medilab.staff.manage.petugas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilab.R
import com.example.medilab.databinding.FragmentManagePetugasBinding
import com.example.medilab.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class ManagePetugasFragment : Fragment() {
    private var _binding: FragmentManagePetugasBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ManagePetugasViewModel
    private lateinit var adapter: PetugasAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManagePetugasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ManagePetugasViewModel::class.java]

        adapter = PetugasAdapter()
        binding.rvPetugas.adapter = adapter

        binding.btnTambah.setOnClickListener { showCreateDialog() }

        viewModel.petugasList.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.load()
    }

    private fun showCreateDialog() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_staff, null)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Tambah Petugas/Admin")
            .setView(view)
            .setPositiveButton("Buat") { _, _ ->
                val nama = view.findViewById<TextInputEditText>(R.id.etNamaStaff).text.toString()
                val email = view.findViewById<TextInputEditText>(R.id.etEmailStaff).text.toString()
                val password = view.findViewById<TextInputEditText>(R.id.etPasswordStaff).text.toString()
                val role = view.findViewById<TextInputEditText>(R.id.etRoleStaff).text.toString()
                val noHP = view.findViewById<TextInputEditText>(R.id.etNoHPStaff).text.toString()
                viewModel.create(email, password, nama, role, noHP)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PetugasAdapter : ListAdapter<User, PetugasAdapter.ViewHolder>(object : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(old: User, new: User) = old.id == new.id
    override fun areContentsTheSame(old: User, new: User) = old == new
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pasien, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvId).text = user.id
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvNama).text = user.nama
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvRm).text = user.role.uppercase()
    }
    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view)
}
