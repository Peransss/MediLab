package com.example.medilab.database.repository

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.sync.SyncManager
import com.example.medilab.model.HasilParameter
import com.example.medilab.model.ResepItem
import com.example.medilab.model.RumahSakit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class LocalLaporanRepository(
    private val database: AppDatabase,
    private val syncManager: SyncManager
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, e -> android.util.Log.e("LocalRepo", "Sync error", e) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)
    private val dao = database.laporanDao()

    fun getAll(): Flow<List<LaporanEntity>> = dao.getAll()

    fun getById(id: String): Flow<LaporanEntity?> = dao.getById(id)

    fun getByStatus(status: String): Flow<List<LaporanEntity>> = dao.getByStatus(status)

    fun getByPasienId(pasienId: String): Flow<List<LaporanEntity>> = dao.getByPasienId(pasienId)

    fun getByPasienIdAndStatus(pasienId: String, status: String): Flow<List<LaporanEntity>> =
        dao.getByPasienIdAndStatus(pasienId, status)

    fun upsert(laporan: LaporanEntity) {
        scope.launch {
            dao.upsert(laporan.copy(syncStatus = SyncStatus.PENDING_CREATE))
            syncManager.pushPendingChanges()
        }
    }

    fun updateStatus(id: String, newStatus: String) {
        scope.launch {
            val current = database.laporanDao().getById(id).first()
            val entity = current?.copy(
                status = newStatus,
                syncStatus = SyncStatus.PENDING_UPDATE,
                lastModifiedAt = System.currentTimeMillis()
            ) ?: return@launch
            dao.upsert(entity)
            syncManager.pushPendingChanges()
        }
    }

    fun update(
        id: String,
        hasilParameter: List<HasilParameter>? = null,
        diagnosa: String? = null,
        resepObat: List<ResepItem>? = null,
        catatanRevisi: String? = null,
        alasanTolak: String? = null,
        rumahSakit: RumahSakit? = null
    ) {
        scope.launch {
            val current = database.laporanDao().getById(id).first() ?: return@launch
            val entity = current.copy(
                hasilParameter = hasilParameter ?: current.hasilParameter,
                diagnosa = diagnosa ?: current.diagnosa,
                resepObat = resepObat ?: current.resepObat,
                catatanRevisi = catatanRevisi ?: current.catatanRevisi,
                alasanTolak = alasanTolak ?: current.alasanTolak,
                rumahSakit = rumahSakit ?: current.rumahSakit,
                syncStatus = SyncStatus.PENDING_UPDATE,
                lastModifiedAt = System.currentTimeMillis()
            )
            dao.upsert(entity)
            syncManager.pushPendingChanges()
        }
    }
}
