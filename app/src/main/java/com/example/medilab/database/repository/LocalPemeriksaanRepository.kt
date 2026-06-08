package com.example.medilab.database.repository

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.PemeriksaanEntity
import com.example.medilab.database.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LocalPemeriksaanRepository(
    private val database: AppDatabase,
    private val syncManager: SyncManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dao = database.pemeriksaanDao()

    fun getAll(): Flow<List<PemeriksaanEntity>> = dao.getAll()

    fun getById(id: String): Flow<PemeriksaanEntity?> = dao.getById(id)

    fun upsert(entity: PemeriksaanEntity) {
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
