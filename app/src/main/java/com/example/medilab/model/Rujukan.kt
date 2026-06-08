package com.example.medilab.model

data class Rujukan(
    val id: String = "",
    val pasienId: String = "",
    val dokterId: String = "",
    val pemeriksaanId: String = "",
    val catatan: String = "",
    val createdAt: Any = System.currentTimeMillis(),
    val status: String = "diterima"
) {
    val createdAtMillis: Long get() = when (val ca = createdAt) {
        is Long -> ca
        is String -> ca.toLongOrNull() ?: System.currentTimeMillis()
        is Number -> ca.toLong()
        else -> System.currentTimeMillis()
    }
}
