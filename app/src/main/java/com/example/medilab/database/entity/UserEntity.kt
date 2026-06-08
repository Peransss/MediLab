package com.example.medilab.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.medilab.database.SyncStatus

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val nama: String = "",
    val email: String = "",
    val role: String = "",
    val noHP: String = "",
    val alamat: String = "",
    val fotoProfile: String = "",
    val noRekamMedis: String = "",
    val tanggalLahir: String = "",
    val createdAt: Long = 0L,
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val lastModifiedAt: Long = System.currentTimeMillis()
)
