package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus
import com.example.medilab.model.ParameterTes

@Entity(tableName = "pemeriksaan")
data class PemeriksaanEntity(
    @PrimaryKey val id: String,
    val namaPemeriksaan: String = "",
    val kategori: String = "",
    val deskripsi: String = "",
    val parameter: List<ParameterTes> = emptyList(),
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
