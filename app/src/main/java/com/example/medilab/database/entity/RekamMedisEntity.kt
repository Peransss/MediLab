package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus
import com.example.medilab.model.RumahSakit

@Entity(tableName = "rekam_medis")
data class RekamMedisEntity(
    @PrimaryKey val id: String,
    val pasienId: String = "",
    val laporanId: String = "",
    val diagnosa: String = "",
    val hasilRingkasan: String = "",
    val rumahSakit: RumahSakit = RumahSakit(),
    val waktu: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
