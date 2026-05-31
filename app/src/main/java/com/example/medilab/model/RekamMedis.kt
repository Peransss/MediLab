package com.example.medilab.model

data class RekamMedis(
    val id: String = "",
    val pasienId: String = "",
    val laporanId: String = "",
    val diagnosa: String = "",
    val hasilRingkasan: String = "",
    val rumahSakit: RumahSakit = RumahSakit(),
    val waktu: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
