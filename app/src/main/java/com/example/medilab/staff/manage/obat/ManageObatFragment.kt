package com.example.medilab.staff.manage.obat

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
import com.example.medilab.databinding.FragmentManageObatBinding
import com.example.medilab.model.Obat

class ManageObatFragment : Fragment() {
    private var _binding: FragmentManageObatBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ManageObatViewModel
    private lateinit var adapter: ObatAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentManageObatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ManageObatViewModel::class.java]

        adapter = ObatAdapter()
        binding.rvObat.adapter = adapter

        viewModel.list.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ObatAdapter : ListAdapter<Obat, ObatAdapter.ViewHolder>(object : DiffUtil.ItemCallback<Obat>() {
    override fun areItemsTheSame(old: Obat, new: Obat) = old.id == new.id
    override fun areContentsTheSame(old: Obat, new: Obat) = old == new
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pasien, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvId).text = item.id
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvNama).text = item.namaObat
        holder.itemView.findViewById<android.widget.TextView>(R.id.tvRm).text = "${item.bentuk} ${item.dosis}${item.satuan}"
    }
    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view)
}
