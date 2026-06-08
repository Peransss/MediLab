package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus

@Entity(tableName = "notifikasi")
data class NotifikasiEntity(
    @PrimaryKey val id: String,
    val userId: String = "",
    val judul: String = "",
    val pesan: String = "",
    val dibaca: Boolean = false,
    val createdAt: Long = 0L,
    val tipe: String = "",
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
