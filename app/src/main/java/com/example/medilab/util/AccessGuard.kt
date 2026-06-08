package com.example.medilab.util

import com.example.medilab.model.User

object AccessGuard {
    fun canManageUsers(user: User): Boolean = user.role == Constants.ROLE_ADMIN
    fun canManagePemeriksaan(user: User): Boolean = user.role == Constants.ROLE_DOKTER || user.role == Constants.ROLE_ADMIN
    fun canAddToQueue(user: User): Boolean = user.role == Constants.ROLE_PASIEN
}
