package com.example.medilab.model

data class Notifikasi(
    val id: String = "",
    val userId: String = "",
    val judul: String = "",
    val pesan: String = "",
    val dibaca: Boolean = false,
    val createdAt: Any = System.currentTimeMillis(),
    val tipe: String = ""
) {
    val createdAtMillis: Long get() = when (val ca = createdAt) {
        is Long -> ca
        is String -> ca.toLongOrNull() ?: System.currentTimeMillis()
        is Number -> ca.toLong()
        else -> System.currentTimeMillis()
    }
}
