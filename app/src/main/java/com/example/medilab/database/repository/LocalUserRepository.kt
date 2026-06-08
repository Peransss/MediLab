package com.example.medilab.database.repository

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.UserEntity
import com.example.medilab.database.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class LocalUserRepository(
    private val database: AppDatabase,
    private val syncManager: SyncManager
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, e -> android.util.Log.e("LocalRepo", "Sync error", e) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)
    private val dao = database.userDao()

    fun getAll(): Flow<List<UserEntity>> = dao.getAll()

    fun getById(id: String): Flow<UserEntity?> = dao.getById(id)

    fun getByRole(role: String): Flow<List<UserEntity>> = dao.getByRole(role)

    fun upsert(user: UserEntity) {
        scope.launch {
            dao.upsert(user.copy(syncStatus = SyncStatus.PENDING_CREATE))
            syncManager.pushPendingChanges()
        }
    }

    fun update(id: String, nama: String, noHP: String, alamat: String) {
        scope.launch {
            val current = database.userDao().getById(id).first()
            val entity = current?.copy(
                nama = nama, noHP = noHP, alamat = alamat,
                syncStatus = SyncStatus.PENDING_UPDATE,
                lastModifiedAt = System.currentTimeMillis()
            ) ?: return@launch
            dao.upsert(entity)
            syncManager.pushPendingChanges()
        }
    }

    fun delete(id: String) {
        scope.launch {
            dao.updateSyncStatus(id, SyncStatus.PENDING_DELETE)
            syncManager.pushPendingChanges()
        }
    }
}
