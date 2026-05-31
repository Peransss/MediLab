package com.example.medilab.model

data class Obat(
    val id: String = "",
    val namaObat: String = "",
    val bentuk: String = "",
    val dosis: Double = 0.0,
    val satuan: String = "",
    val keterangan: String = ""
)
