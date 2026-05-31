package com.example.medilab.model

data class AuditLog(
    val id: String = "",
    val userId: String = "",
    val aksi: String = "",
    val targetId: String = "",
    val targetTipe: String = "",
    val detail: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
