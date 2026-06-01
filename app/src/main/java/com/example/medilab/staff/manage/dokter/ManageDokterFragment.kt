package com.example.medilab.staff.manage.dokter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilab.R
import com.example.medilab.databinding.FragmentManageDokterBinding
import com.example.medilab.model.Dokter

class ManageDokterFragment : Fragment() {
    private var _binding: FragmentManageDokterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ManageDokterViewModel
    private lateinit var adapter: DokterAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageDokterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ManageDokterViewModel::class.java]

        adapter = DokterAdapter { dokter ->
            Toast.makeText(requireContext(), "${dokter.nama} - ${dokter.id}", Toast.LENGTH_SHORT).show()
        }
        binding.rvDokter.adapter = adapter

        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        viewModel.dokterList.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class DokterAdapter(private val onClick: (Dokter) -> Unit) :
    ListAdapter<Dokter, DokterAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Dokter>() {
        override fun areItemsTheSame(old: Dokter, new: Dokter) = old.id == new.id
        override fun areContentsTheSame(old: Dokter, new: Dokter) = old == new
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pasien, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dokter = getItem(position)
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvId).text = dokter.id
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvNama).text = dokter.nama
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvRm).text = dokter.spesialis
        holder.itemView.setOnClickListener { onClick(dokter) }
    }

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view)
}
