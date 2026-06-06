package com.example.medilab.ui.util

import androidx.compose.ui.graphics.Color

data class StatusColor(val background: Color, val text: Color)

object StatusColors {
    val BARU = StatusColor(Color(0xFFE8E8FF), Color(0xFF1A1A66))
    val PROSES = StatusColor(Color(0xFFFEF3C7), Color(0xFF92400E))
    val VERIFIKASI = StatusColor(Color(0xFFDBEAFE), Color(0xFF1E40AF))
    val REVISI = StatusColor(Color(0xFFFEF3C7), Color(0xFF92400E))
    val SELESAI = StatusColor(Color(0xFFD1FAE5), Color(0xFF065F46))
    val DITOLAK = StatusColor(Color(0xFFFEE2E2), Color(0xFF991B1B))
    val BATAL = StatusColor(Color(0xFFF3F4F6), Color(0xFF374151))

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
