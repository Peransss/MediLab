package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifikasis")
data class NotifikasiEntity(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val judul: String = "",
    val pesan: String = "",
    val dibaca: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val tipe: String = "",
    val syncStatus: String = "SYNCED"
)
