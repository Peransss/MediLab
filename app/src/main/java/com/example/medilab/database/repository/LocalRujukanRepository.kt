package com.example.medilab.database.repository

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.RujukanEntity
import com.example.medilab.database.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class LocalRujukanRepository(
    private val database: AppDatabase,
    private val syncManager: SyncManager
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, e -> android.util.Log.e("LocalRepo", "Sync error", e) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)
    private val dao = database.rujukanDao()

    fun getAll(): Flow<List<RujukanEntity>> = dao.getAll()

    fun getById(id: String): Flow<RujukanEntity?> = dao.getById(id)

    fun upsert(entity: RujukanEntity) {
        scope.launch {
            dao.upsert(entity.copy(syncStatus = SyncStatus.PENDING_CREATE))
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
