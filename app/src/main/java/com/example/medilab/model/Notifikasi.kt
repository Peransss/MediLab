package com.example.medilab.model

data class Notifikasi(
    val id: String = "",
    val userId: String = "",
    val judul: String = "",
    val pesan: String = "",
    val dibaca: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val tipe: String = ""
)
