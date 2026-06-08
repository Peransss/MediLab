package com.example.medilab.database.sync

import com.example.medilab.database.AppDatabase
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.DokterEntity
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.entity.NotifikasiEntity
import com.example.medilab.database.entity.ObatEntity
import com.example.medilab.database.entity.PemeriksaanEntity
import com.example.medilab.database.entity.UserEntity
import com.example.medilab.model.Dokter
import com.example.medilab.model.Laporan
import com.example.medilab.model.Notifikasi
import com.example.medilab.model.Obat
import com.example.medilab.model.Pemeriksaan
import com.example.medilab.repository.DokterRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.repository.ObatRepository
import com.example.medilab.repository.PemeriksaanRepository
import com.example.medilab.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SyncManager(
    private val database: AppDatabase,
    private val userRepository: UserRepository,
    private val laporanRepository: LaporanRepository,
    private val notifikasiRepository: NotifikasiRepository,
    private val pemeriksaanRepository: PemeriksaanRepository,
    private val obatRepository: ObatRepository,
    private val dokterRepository: DokterRepository,
    private val networkMonitor: NetworkMonitor,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    var lastSyncTimestamp: Long = 0L

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
        pushPendingUsers()
        pushPendingLaporans()
        pushPendingNotifikasis()
        pushPendingPemeriksaans()
        pushPendingObats()
        pushPendingDokters()
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

    suspend fun pullRemoteChanges() {
        pullUpdatedUsers()
        pullUpdatedLaporans()
        pullUpdatedPemeriksaans()
        pullUpdatedObats()
        pullUpdatedDokters()
        lastSyncTimestamp = System.currentTimeMillis()
    }

    private suspend fun pullUpdatedUsers() {
        userRepository.getAllUsers { users ->
            scope.launch {
                val entities = users.map { user ->
                    UserEntity(
                        id = user.id,
                        nama = user.nama,
                        email = user.email,
                        role = user.role,
                        noHP = user.noHP,
                        alamat = user.alamat,
                        fotoProfile = user.fotoProfile,
                        noRekamMedis = user.noRekamMedis,
                        tanggalLahir = user.tanggalLahir,
                        createdAt = user.createdAt,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = user.createdAt
                    )
                }
                database.userDao().upsertAll(entities)
            }
        }
    }

    private suspend fun pullUpdatedLaporans() {
        laporanRepository.getAll { laporans ->
            scope.launch {
                val entities = laporans.map { l ->
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
                }
                database.laporanDao().upsertAll(entities)
            }
        }
    }

    private suspend fun pullUpdatedPemeriksaans() {
        pemeriksaanRepository.getAll { items ->
            scope.launch {
                val entities = items.map { p ->
                    PemeriksaanEntity(
                        id = p.id, namaPemeriksaan = p.namaPemeriksaan,
                        kategori = p.kategori, deskripsi = p.deskripsi,
                        parameter = p.parameter,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                }
                database.pemeriksaanDao().upsertAll(entities)
            }
        }
    }

    private suspend fun pullUpdatedObats() {
        obatRepository.getAll { items ->
            scope.launch {
                val entities = items.map { o ->
                    ObatEntity(
                        id = o.id, namaObat = o.namaObat,
                        bentuk = o.bentuk, dosis = o.dosis,
                        satuan = o.satuan, keterangan = o.keterangan,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                }
                database.obatDao().upsertAll(entities)
            }
        }
    }

    private suspend fun pullUpdatedDokters() {
        dokterRepository.getAll { items ->
            scope.launch {
                val entities = items.map { d ->
                    DokterEntity(
                        id = d.id, nama = d.nama,
                        spesialis = d.spesialis, alamat = d.alamat,
                        noHP = d.noHP, email = d.email,
                        syncStatus = SyncStatus.SYNCED,
                        lastModifiedAt = System.currentTimeMillis()
                    )
                }
                database.dokterDao().upsertAll(entities)
            }
        }
    }
}
