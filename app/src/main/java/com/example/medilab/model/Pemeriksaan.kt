package com.example.medilab.model

data class ParameterTes(
    val nama: String = "",
    val satuan: String = "",
    val nilaiNormalMin: Double = 0.0,
    val nilaiNormalMax: Double = 0.0
)

data class Pemeriksaan(
    val id: String = "",
    val namaPemeriksaan: String = "",
    val kategori: String = "",
    val deskripsi: String = "",
    val parameter: List<ParameterTes> = emptyList()
)
