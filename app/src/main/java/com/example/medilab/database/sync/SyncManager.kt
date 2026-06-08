package com.example.medilab.database.sync

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.AuditLogEntity
import com.example.medilab.database.entity.DokterEntity
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.entity.NotifikasiEntity
import com.example.medilab.database.entity.ObatEntity
import com.example.medilab.database.entity.PemeriksaanEntity
import com.example.medilab.database.entity.RekamMedisEntity
import com.example.medilab.database.entity.RujukanEntity
import com.example.medilab.database.entity.UserEntity
import com.example.medilab.model.AuditLog
import com.example.medilab.model.Dokter
import com.example.medilab.model.Laporan
import com.example.medilab.model.Notifikasi
import com.example.medilab.model.Obat
import com.example.medilab.model.Pemeriksaan
import com.example.medilab.model.RekamMedis
import com.example.medilab.model.Rujukan
import com.example.medilab.repository.AuditLogRepository
import com.example.medilab.repository.DokterRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.repository.ObatRepository
import com.example.medilab.repository.PemeriksaanRepository
import com.example.medilab.repository.RekamMedisRepository
import com.example.medilab.repository.RujukanRepository
import com.example.medilab.repository.UserRepository
import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.sync.withLock

class SyncManager(
    private val database: AppDatabase,
    private val userRepository: UserRepository,
    private val laporanRepository: LaporanRepository,
    private val notifikasiRepository: NotifikasiRepository,
    private val pemeriksaanRepository: PemeriksaanRepository,
    private val obatRepository: ObatRepository,
    private val dokterRepository: DokterRepository,
    private val rekamMedisRepository: RekamMedisRepository,
    private val rujukanRepository: RujukanRepository,
    private val auditLogRepository: AuditLogRepository,
    private val networkMonitor: NetworkMonitor,
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, e -> Log.e("SyncManager", "Unhandled error", e) }
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)

    @Volatile
    var lastSyncTimestamp: Long = 0L
    private val syncMutex = Mutex()

    fun startListening() {
        scope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online) {
                    pushPendingChanges()
                    pullRemoteChanges()
                }
            }
        }
    }

    suspend fun pushPendingChanges() {
        syncMutex.withLock {
        pushPendingUsers()
        pushPendingLaporans()
        pushPendingNotifikasis()
        pushPendingPemeriksaans()
        pushPendingObats()
        pushPendingDokters()
        pushPendingRekamMedis()
        pushPendingRujukans()
        pushPendingAuditLogs()
        }
    }

    private suspend fun pushPendingUsers() {
        val pending = database.userDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE, SyncStatus.PENDING_UPDATE -> {
                    val data = mapOf<String, Any>(
                        "nama" to entity.nama,
                        "email" to entity.email,
                        "role" to entity.role,
                        "noHP" to entity.noHP,
                        "alamat" to entity.alamat,
                        "fotoProfile" to entity.fotoProfile,
                        "noRekamMedis" to entity.noRekamMedis,
                        "tanggalLahir" to entity.tanggalLahir,
                        "createdAt" to entity.createdAt
                    )
                    userRepository.updateUser(entity.id, data) { success ->
                        if (success) {
                            scope.launch {
                                database.userDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                            }
                        }
                    }
                }
                SyncStatus.PENDING_DELETE -> {
                    userRepository.deleteUser(entity.id) { success ->
                        if (success) {
                            scope.launch {
                                database.userDao().deleteById(entity.id)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun pushPendingLaporans() {
        val pending = database.laporanDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE -> {
                    val laporan = Laporan(
                        id = entity.id,
                        status = entity.status,
                        pasienId = entity.pasienId,
                        dokterId = entity.dokterId,
                        petugasId = entity.petugasId,
                        adminId = entity.adminId,
                        pemeriksaanId = entity.pemeriksaanId,
                        rujukanId = entity.rujukanId,
                        hasilParameter = entity.hasilParameter,
                        diagnosa = entity.diagnosa,
                        resepObat = entity.resepObat,
                        catatanRevisi = entity.catatanRevisi,
                        alasanTolak = entity.alasanTolak,
                        rumahSakit = entity.rumahSakit,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt,
                        tanggalSelesai = entity.tanggalSelesai
                    )
                    laporanRepository.add(laporan) { success ->
                        if (success) scope.launch {
                            database.laporanDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                SyncStatus.PENDING_UPDATE -> {
                    val data = mapOf<String, Any>(
                        "status" to entity.status,
                        "hasilParameter" to entity.hasilParameter,
                        "diagnosa" to entity.diagnosa,
                        "resepObat" to entity.resepObat,
                        "catatanRevisi" to entity.catatanRevisi,
                        "alasanTolak" to entity.alasanTolak,
                        "updatedAt" to entity.lastModifiedAt,
                        "tanggalSelesai" to entity.tanggalSelesai
                    )
                    laporanRepository.update(entity.id, data) { success ->
                        if (success) scope.launch {
                            database.laporanDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun pushPendingNotifikasis() {
        val pending = database.notifikasiDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE -> {
                    val notifikasi = Notifikasi(
                        id = entity.id,
                        userId = entity.userId,
                        judul = entity.judul,
                        pesan = entity.pesan,
                        dibaca = entity.dibaca,
                        createdAt = entity.createdAt,
                        tipe = entity.tipe
                    )
                    notifikasiRepository.add(notifikasi) { success ->
                        if (success) scope.launch {
                            database.notifikasiDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                SyncStatus.PENDING_UPDATE -> {
                    notifikasiRepository.markAsRead(entity.id) { success ->
                        if (success) scope.launch {
                            database.notifikasiDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun pushPendingPemeriksaans() {
        val pending = database.pemeriksaanDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE, SyncStatus.PENDING_UPDATE -> {
                    val pemeriksaan = Pemeriksaan(
                        id = entity.id, namaPemeriksaan = entity.namaPemeriksaan,
                        kategori = entity.kategori, deskripsi = entity.deskripsi,
                        parameter = entity.parameter
                    )
                    pemeriksaanRepository.add(pemeriksaan) { success ->
                        if (success) scope.launch {
                            database.pemeriksaanDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                SyncStatus.PENDING_DELETE -> {
                    pemeriksaanRepository.delete(entity.id) { success ->
                        if (success) scope.launch {
                            database.pemeriksaanDao().deleteById(entity.id)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun pushPendingObats() {
        val pending = database.obatDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE, SyncStatus.PENDING_UPDATE -> {
                    val obat = Obat(
                        id = entity.id, namaObat = entity.namaObat,
                        bentuk = entity.bentuk, dosis = entity.dosis,
                        satuan = entity.satuan, keterangan = entity.keterangan
                    )
                    obatRepository.add(obat) { success ->
                        if (success) scope.launch {
                            database.obatDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                SyncStatus.PENDING_DELETE -> {
                    obatRepository.delete(entity.id) { success ->
                        if (success) scope.launch {
                            database.obatDao().deleteById(entity.id)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun pushPendingDokters() {
        val pending = database.dokterDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE, SyncStatus.PENDING_UPDATE -> {
                    val dokter = Dokter(
                        id = entity.id, nama = entity.nama,
                        spesialis = entity.spesialis, alamat = entity.alamat,
                        noHP = entity.noHP, email = entity.email
                    )
                    dokterRepository.add(dokter) { success ->
                        if (success) scope.launch {
                            database.dokterDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                SyncStatus.PENDING_DELETE -> {
                    dokterRepository.delete(entity.id) { success ->
                        if (success) scope.launch {
                            database.dokterDao().deleteById(entity.id)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun pushPendingRekamMedis() {
        val pending = database.rekamMedisDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE, SyncStatus.PENDING_UPDATE -> {
                    val rekamMedis = RekamMedis(
                        id = entity.id, pasienId = entity.pasienId,
                        laporanId = entity.laporanId, diagnosa = entity.diagnosa,
                        hasilRingkasan = entity.hasilRingkasan,
                        rumahSakit = entity.rumahSakit, waktu = entity.waktu,
                        createdAt = entity.createdAt
                    )
                    rekamMedisRepository.add(rekamMedis) { success ->
                        if (success) scope.launch {
                            database.rekamMedisDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                SyncStatus.PENDING_DELETE -> {
                    rekamMedisRepository.delete(entity.id) { success ->
                        if (success) scope.launch {
                            database.rekamMedisDao().deleteById(entity.id)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun pushPendingRujukans() {
        val pending = database.rujukanDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE, SyncStatus.PENDING_UPDATE -> {
                    val rujukan = Rujukan(
                        id = entity.id, pasienId = entity.pasienId,
                        dokterId = entity.dokterId, pemeriksaanId = entity.pemeriksaanId,
                        catatan = entity.catatan, createdAt = entity.createdAt,
                        status = entity.status
                    )
                    rujukanRepository.add(rujukan) { success ->
                        if (success) scope.launch {
                            database.rujukanDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                SyncStatus.PENDING_DELETE -> {
                    rujukanRepository.delete(entity.id) { success ->
                        if (success) scope.launch {
                            database.rujukanDao().deleteById(entity.id)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun pushPendingAuditLogs() {
        val pending = database.auditLogDao().getPendingSync()
        for (entity in pending) {
            when (entity.syncStatus) {
                SyncStatus.PENDING_CREATE, SyncStatus.PENDING_UPDATE -> {
                    val auditLog = AuditLog(
                        id = entity.id, userId = entity.userId,
                        aksi = entity.aksi, targetId = entity.targetId,
                        targetTipe = entity.targetTipe, detail = entity.detail,
                        timestamp = entity.timestamp
                    )
                    auditLogRepository.add(auditLog) { success ->
                        if (success) scope.launch {
                            database.auditLogDao().updateSyncStatus(entity.id, SyncStatus.SYNCED)
                        }
                    }
                }
                SyncStatus.PENDING_DELETE -> {
                    auditLogRepository.delete(entity.id) { success ->
                        if (success) scope.launch {
                            database.auditLogDao().deleteById(entity.id)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    suspend fun pullRemoteChanges() {
        syncMutex.withLock {
        pullUpdatedUsers()
        pullUpdatedLaporans()
        pullUpdatedPemeriksaans()
        pullUpdatedObats()
        pullUpdatedDokters()
        pullUpdatedRekamMedis()
        pullUpdatedRujukans()
        pullUpdatedAuditLogs()
        lastSyncTimestamp = System.currentTimeMillis()
        }
    }

    private suspend fun <T> syncCollection(
        query: ((List<T>) -> Unit) -> Unit,
        upsert: suspend (List<T>) -> Unit
    ) {
        val deferred = CompletableDeferred<Unit>()
        query { items ->
            scope.launch {
                try {
                    upsert(items)
                } catch (e: Exception) {
                    Log.e("SyncManager", "Sync error", e)
                } finally {
                    deferred.complete(Unit)
                }
            }
        }
        try {
            withTimeout(30_000L) { deferred.await() }
        } catch (_: TimeoutCancellationException) {
            Log.e("SyncManager", "Sync timed out after 30s")
        }
    }

    suspend fun pullUserById(uid: String): UserEntity? {
        val deferred = CompletableDeferred<UserEntity?>()
        userRepository.getUser(uid) { user ->
            val entity = user?.let {
                UserEntity(
                    id = it.id, nama = it.nama, email = it.email,
                    role = it.role, noHP = it.noHP, alamat = it.alamat,
                    fotoProfile = it.fotoProfile, noRekamMedis = it.noRekamMedis,
                    tanggalLahir = it.tanggalLahir, createdAt = it.createdAt,
                    syncStatus = SyncStatus.SYNCED, lastModifiedAt = it.createdAt
                )
            }
            if (entity != null) {
                scope.launch {
                    database.userDao().upsert(entity)
                    deferred.complete(entity)
                }
            } else {
                deferred.complete(null)
            }
        }
        return try {
            withTimeout(15_000L) { deferred.await() }
        } catch (_: TimeoutCancellationException) {
            Log.e("SyncManager", "pullUserById timed out for $uid")
            null
        }
    }

    private suspend fun pullUpdatedUsers() {
        syncCollection(
            query = { cb -> userRepository.getAllUsers(cb) },
            upsert = { users ->
                database.userDao().upsertAll(users.map { user ->
                    UserEntity(
                        id = user.id, nama = user.nama, email = user.email,
                        role = user.role, noHP = user.noHP, alamat = user.alamat,
                        fotoProfile = user.fotoProfile, noRekamMedis = user.noRekamMedis,
                        tanggalLahir = user.tanggalLahir, createdAt = user.createdAt,
                        syncStatus = SyncStatus.SYNCED, lastModifiedAt = user.createdAt
                    )
                })
            }
        )
    }

    private suspend fun pullUpdatedLaporans() {
        syncCollection(
            query = { cb -> laporanRepository.getAll(cb) },
            upsert = { laporans ->
                database.laporanDao().upsertAll(laporans.map { l ->
                    LaporanEntity(
                        id = l.id, status = l.status, pasienId = l.pasienId,
                        dokterId = l.dokterId, petugasId = l.petugasId,
                        adminId = l.adminId, pemeriksaanId = l.pemeriksaanId,
                        rujukanId = l.rujukanId, hasilParameter = l.hasilParameter,
                        diagnosa = l.diagnosa, resepObat = l.resepObat,
                        catatanRevisi = l.catatanRevisi, alasanTolak = l.alasanTolak,
                        rumahSakit = l.rumahSakit, createdAt = l.createdAt,
                        updatedAt = l.updatedAt, tanggalSelesai = l.tanggalSelesai,
                        syncStatus = SyncStatus.SYNCED, lastModifiedAt = l.updatedAt
                    )
                })
            }
        )
    }

    private suspend fun pullUpdatedPemeriksaans() {
        syncCollection(
            query = { cb -> pemeriksaanRepository.getAll(cb) },
            upsert = { items ->
                database.pemeriksaanDao().upsertAll(items.map { p ->
                    PemeriksaanEntity(
                        id = p.id, namaPemeriksaan = p.namaPemeriksaan,
                        kategori = p.kategori, deskripsi = p.deskripsi,
                        parameter = p.parameter,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                })
            }
        )
    }

    private suspend fun pullUpdatedObats() {
        syncCollection(
            query = { cb -> obatRepository.getAll(cb) },
            upsert = { items ->
                database.obatDao().upsertAll(items.map { o ->
                    ObatEntity(
                        id = o.id, namaObat = o.namaObat,
                        bentuk = o.bentuk, dosis = o.dosis,
                        satuan = o.satuan, keterangan = o.keterangan,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                })
            }
        )
    }

    private suspend fun pullUpdatedDokters() {
        syncCollection(
            query = { cb -> dokterRepository.getAll(cb) },
            upsert = { items ->
                database.dokterDao().upsertAll(items.map { d ->
                    DokterEntity(
                        id = d.id, nama = d.nama,
                        spesialis = d.spesialis, alamat = d.alamat,
                        noHP = d.noHP, email = d.email,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                })
            }
        )
    }

    private suspend fun pullUpdatedRekamMedis() {
        syncCollection(
            query = { cb -> rekamMedisRepository.getAll(cb) },
            upsert = { items ->
                database.rekamMedisDao().upsertAll(items.map { r ->
                    RekamMedisEntity(
                        id = r.id, pasienId = r.pasienId,
                        laporanId = r.laporanId, diagnosa = r.diagnosa,
                        hasilRingkasan = r.hasilRingkasan,
                        rumahSakit = r.rumahSakit, waktu = r.waktu,
                        createdAt = r.createdAt,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                })
            }
        )
    }

    private suspend fun pullUpdatedRujukans() {
        syncCollection(
            query = { cb -> rujukanRepository.getAll(cb) },
            upsert = { items ->
                database.rujukanDao().upsertAll(items.map { r ->
                    RujukanEntity(
                        id = r.id, pasienId = r.pasienId,
                        dokterId = r.dokterId, pemeriksaanId = r.pemeriksaanId,
                        catatan = r.catatan, createdAt = r.createdAt,
                        status = r.status,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                })
            }
        )
    }

    private suspend fun pullUpdatedAuditLogs() {
        syncCollection(
            query = { cb -> auditLogRepository.getAll(cb) },
            upsert = { items ->
                database.auditLogDao().upsertAll(items.map { a ->
                    AuditLogEntity(
                        id = a.id, userId = a.userId,
                        aksi = a.aksi, targetId = a.targetId,
                        targetTipe = a.targetTipe, detail = a.detail,
                        timestamp = a.timestamp,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                })
            }
        )
    }
}
