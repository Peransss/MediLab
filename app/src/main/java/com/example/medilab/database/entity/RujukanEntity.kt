package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus

@Entity(tableName = "rujukan")
data class RujukanEntity(
    @PrimaryKey val id: String,
    val pasienId: String = "",
    val dokterId: String = "",
    val pemeriksaanId: String = "",
    val catatan: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "diterima",
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
