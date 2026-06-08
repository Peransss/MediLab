package com.example.medilab.model

data class RekamMedis(
    val id: String = "",
    val pasienId: String = "",
    val laporanId: String = "",
    val diagnosa: String = "",
    val hasilRingkasan: String = "",
    val rumahSakit: RumahSakit = RumahSakit(),
    val waktu: String = "",
    val createdAt: Any = System.currentTimeMillis()
) {
    val createdAtMillis: Long get() = when (val ca = createdAt) {
        is Long -> ca
        is String -> ca.toLongOrNull() ?: System.currentTimeMillis()
        is Number -> ca.toLong()
        else -> System.currentTimeMillis()
    }
}
