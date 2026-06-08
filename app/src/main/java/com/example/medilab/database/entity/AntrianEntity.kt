package com.example.medilab.database.entity

data class AntrianEntity(
    val id: String = "",
    val pasienId: String = "",
    val status: String = "menunggu",
    val waktuDaftar: Long = System.currentTimeMillis(),
    val nomorAntrian: Int = 0,
    val tujuan: String = ""
)
