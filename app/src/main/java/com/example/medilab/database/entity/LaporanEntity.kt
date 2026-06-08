package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus
import com.example.medilab.model.HasilParameter
import com.example.medilab.model.ResepItem
import com.example.medilab.model.RumahSakit

@Entity(tableName = "laporan")
data class LaporanEntity(
    @PrimaryKey val id: String,
    val status: String = "",
    val pasienId: String = "",
    val dokterId: String = "",
    val petugasId: String = "",
    val adminId: String = "",
    val pemeriksaanId: String = "",
    val rujukanId: String = "",
    val hasilParameter: List<HasilParameter> = emptyList(),
    val diagnosa: String = "",
    val resepObat: List<ResepItem> = emptyList(),
    val catatanRevisi: String = "",
    val alasanTolak: String = "",
    val rumahSakit: RumahSakit = RumahSakit(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val tanggalSelesai: Long = 0L,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
