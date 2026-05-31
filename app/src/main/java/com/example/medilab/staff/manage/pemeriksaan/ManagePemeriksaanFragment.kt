package com.example.medilab.staff.manage.pemeriksaan

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
import com.example.medilab.databinding.FragmentManagePemeriksaanBinding
import com.example.medilab.model.Pemeriksaan

class ManagePemeriksaanFragment : Fragment() {
    private var _binding: FragmentManagePemeriksaanBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ManagePemeriksaanViewModel
    private lateinit var adapter: PemeriksaanAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManagePemeriksaanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ManagePemeriksaanViewModel::class.java]

        adapter = PemeriksaanAdapter()
        binding.rvPemeriksaan.adapter = adapter

        viewModel.list.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PemeriksaanAdapter : ListAdapter<Pemeriksaan, PemeriksaanAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Pemeriksaan>() {
    override fun areItemsTheSame(old: Pemeriksaan, new: Pemeriksaan) = old.id == new.id
    override fun areContentsTheSame(old: Pemeriksaan, new: Pemeriksaan) = old == new
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pasien, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvId).text = item.id
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvNama).text = item.namaPemeriksaan
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvRm).text = item.kategori
    }
    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view)
}
