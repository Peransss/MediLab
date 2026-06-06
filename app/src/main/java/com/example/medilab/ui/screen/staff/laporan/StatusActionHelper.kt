package com.example.medilab.ui.screen.staff.laporan

import com.example.medilab.util.Constants

data class StatusAction(val label: String, val newStatus: String, val isPrimary: Boolean)

object StatusActionHelper {
    fun actionsFor(role: String, status: String): List<StatusAction> {
        val actions = mutableListOf<StatusAction>()
        when (role) {
            Constants.ROLE_PETUGAS -> when (status) {
                Constants.STATUS_BARU -> {
                    actions += StatusAction("Mulai Proses", Constants.STATUS_PROSES, true)
                    actions += StatusAction("Tolak", Constants.STATUS_DITOLAK, false)
                }
                Constants.STATUS_PROSES -> actions += StatusAction("Kirim Verifikasi", Constants.STATUS_VERIFIKASI, true)
                Constants.STATUS_REVISI -> actions += StatusAction("Kirim Verifikasi Ulang", Constants.STATUS_VERIFIKASI, true)
            }
            Constants.ROLE_ADMIN -> when (status) {
                Constants.STATUS_VERIFIKASI -> {
                    actions += StatusAction("Setujui", Constants.STATUS_SELESAI, true)
                    actions += StatusAction("Minta Revisi", Constants.STATUS_REVISI, false)
                }
            }
        }
        if (status != Constants.STATUS_SELESAI && status != Constants.STATUS_BATAL) {
            actions += StatusAction("Batalkan", Constants.STATUS_BATAL, false)
        }
        return actions
    }
}
