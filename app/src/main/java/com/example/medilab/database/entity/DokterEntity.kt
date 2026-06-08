package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus

@Entity(tableName = "dokter")
data class DokterEntity(
    @PrimaryKey val id: String,
    val nama: String = "",
    val spesialis: String = "",
    val alamat: String = "",
    val noHP: String = "",
    val email: String = "",
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
