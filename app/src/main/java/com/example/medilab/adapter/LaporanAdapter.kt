package com.example.medilab.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.medilab.databinding.ItemLaporanBinding
import com.example.medilab.model.Laporan
import com.example.medilab.util.DateUtils

class LaporanAdapter(private val onClick: (Laporan) -> Unit) :
    ListAdapter<Laporan, LaporanAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLaporanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemLaporanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(laporan: Laporan) {
            binding.tvLaporanId.text = laporan.id
            binding.tvLaporanStatus.text = laporan.status.uppercase()
            binding.tvLaporanTanggal.text = DateUtils.formatDisplay(java.util.Date(laporan.createdAt))
            binding.root.setOnClickListener { onClick(laporan) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Laporan>() {
        override fun areItemsTheSame(old: Laporan, new: Laporan) = old.id == new.id
        override fun areContentsTheSame(old: Laporan, new: Laporan) = old == new
    }
}
