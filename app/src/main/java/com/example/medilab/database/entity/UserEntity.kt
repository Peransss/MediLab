package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "",
    val nama: String = "",
    val email: String = "",
    val role: String = "",
    val noHP: String = "",
    val alamat: String = "",
    val fotoProfile: String = "",
    val noRekamMedis: String = "",
    val tanggalLahir: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val syncStatus: String = "SYNCED"
)
