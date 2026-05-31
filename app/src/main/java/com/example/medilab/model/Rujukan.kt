package com.example.medilab.model

data class Rujukan(
    val id: String = "",
    val pasienId: String = "",
    val dokterId: String = "",
    val pemeriksaanId: String = "",
    val catatan: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "diterima"
)
