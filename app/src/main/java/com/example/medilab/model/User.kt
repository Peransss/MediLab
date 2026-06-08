package com.example.medilab.model

data class User(
    val id: String = "",
    val nama: String = "",
    val email: String = "",
    val role: String = "",
    val noHP: String = "",
    val alamat: String = "",
    val fotoProfile: String = "",
    val noRekamMedis: String = "",
    val tanggalLahir: String = "",
    val createdAt: Any = System.currentTimeMillis()
) {
    val createdAtMillis: Long get() = when (val ca = createdAt) {
        is Long -> ca
        is String -> ca.toLongOrNull() ?: System.currentTimeMillis()
        is Number -> ca.toLong()
        else -> System.currentTimeMillis()
    }
}
