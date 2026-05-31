package com.example.medilab.model

data class HasilParameter(
    val parameterNama: String = "",
    val nilai: Double = 0.0,
    val satuan: String = "",
    val keterangan: String = ""
)

data class ResepItem(
    val obatId: String = "",
    val namaObat: String = "",
    val bentuk: String = "",
    val dosis: Double = 0.0,
    val satuan: String = "",
    val aturanPakai: String = "",
    val jumlah: Int = 0,
    val keterangan: String = ""
)

data class RumahSakit(
    val nama: String = "RS MediLab Sehat",
    val alamat: String = "Jl. Kesehatan No. 1",
    val kota: String = "Jakarta"
)

data class Laporan(
    val id: String = "",
    val status: String = "baru",
    val pasienId: String = "",
    val dokterId: String = "",
    val petugasId: String = "",
    val adminId: String = "",
    val pemeriksaanId: String = "",
    val rujukanId: String = "",
    val hasilParameter: List<HasilParameter> = emptyList(),
    val diagnosa: String = "",
    val resepObat: List<ResepItem> = emptyList(),
    val catatanRevisi: String = "",
    val alasanTolak: String = "",
    val rumahSakit: RumahSakit = RumahSakit(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tanggalSelesai: Long = 0L
)
