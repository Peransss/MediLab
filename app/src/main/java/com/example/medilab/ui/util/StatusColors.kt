package com.example.medilab.ui.util

import androidx.compose.ui.graphics.Color

data class StatusColor(val background: Color, val text: Color)

object StatusColors {
    val BARU = StatusColor(Color(0xFF0891B2).copy(alpha = 0.15f), Color(0xFF0891B2))       // Teal
    val PROSES = StatusColor(Color(0xFF22D3EE).copy(alpha = 0.15f), Color(0xFF0891B2))     // Cyan tint
    val VERIFIKASI = StatusColor(Color(0xFFFF9800).copy(alpha = 0.15f), Color(0xFFFF9800))  // Orange
    val REVISI = StatusColor(Color(0xFFFF9800).copy(alpha = 0.15f), Color(0xFFFF9800))     // Orange
    val SELESAI = StatusColor(Color(0xFF059669).copy(alpha = 0.15f), Color(0xFF059669))    // Green
    val DITOLAK = StatusColor(Color(0xFFEF4444).copy(alpha = 0.15f), Color(0xFFEF4444))    // Red
    val BATAL = StatusColor(Color(0xFF64748B).copy(alpha = 0.15f), Color(0xFF64748B))      // Slate

    fun from(status: String): StatusColor = when (status.lowercase()) {
        "baru" -> BARU
        "proses" -> PROSES
        "verifikasi" -> VERIFIKASI
        "revisi" -> REVISI
        "selesai" -> SELESAI
        "ditolak" -> DITOLAK
        "batal" -> BATAL
        else -> BARU
    }
}
