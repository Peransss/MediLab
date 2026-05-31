package com.example.medilab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilab.databinding.ItemRiwayatBinding
import com.example.medilab.model.RekamMedis
import com.example.medilab.util.DateUtils

class RiwayatAdapter(private val onClick: (RekamMedis) -> Unit) :
    ListAdapter<RekamMedis, RiwayatAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRiwayatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRiwayatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(rm: RekamMedis) {
            binding.tvDiagnosa.text = rm.diagnosa
            binding.tvRumahSakit.text = rm.rumahSakit.nama
            binding.tvWaktu.text = rm.waktu
            binding.root.setOnClickListener { onClick(rm) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RekamMedis>() {
        override fun areItemsTheSame(old: RekamMedis, new: RekamMedis) = old.id == new.id
        override fun areContentsTheSame(old: RekamMedis, new: RekamMedis) = old == new
    }
}
