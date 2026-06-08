package com.example.medilab.database.repository

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.entity.NotifikasiEntity
import com.example.medilab.database.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class LocalNotifikasiRepository(
    private val database: AppDatabase,
    private val syncManager: SyncManager
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, e -> android.util.Log.e("LocalRepo", "Sync error", e) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)
    private val dao = database.notifikasiDao()

    fun getByUserId(userId: String): Flow<List<NotifikasiEntity>> = dao.getByUserId(userId)

    fun upsert(notifikasi: NotifikasiEntity) {
        scope.launch {
            dao.upsert(notifikasi)
            syncManager.pushPendingChanges()
        }
    }

    fun markAsRead(id: String) {
        scope.launch {
            dao.markAsRead(id)
            syncManager.pushPendingChanges()
        }
    }

    fun markAllAsRead(userId: String) {
        scope.launch {
            dao.markAllAsRead(userId)
            syncManager.pushPendingChanges()
        }
    }
}
