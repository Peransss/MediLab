package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus

@Entity(tableName = "obat")
data class ObatEntity(
    @PrimaryKey val id: String,
    val namaObat: String = "",
    val bentuk: String = "",
    val dosis: Double = 0.0,
    val satuan: String = "",
    val keterangan: String = "",
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
