package com.example.medilab.database.repository

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.RekamMedisEntity
import com.example.medilab.database.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LocalRekamMedisRepository(
    private val database: AppDatabase,
    private val syncManager: SyncManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dao = database.rekamMedisDao()

    fun getAll(): Flow<List<RekamMedisEntity>> = dao.getAll()

    fun getByPasienId(pasienId: String): Flow<List<RekamMedisEntity>> = dao.getByPasienId(pasienId)

    fun getById(id: String): Flow<RekamMedisEntity?> = dao.getById(id)

    fun upsert(entity: RekamMedisEntity) {
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
