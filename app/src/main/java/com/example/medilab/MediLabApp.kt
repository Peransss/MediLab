package com.example.medilab

import android.app.Application
import com.example.medilab.database.AppDatabase
import com.example.medilab.database.sync.NetworkMonitor
import com.example.medilab.database.sync.SyncManager
import com.example.medilab.database.sync.SyncWorker
import com.example.medilab.repository.AuditLogRepository
import com.example.medilab.repository.DokterRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.repository.ObatRepository
import com.example.medilab.repository.PemeriksaanRepository
import com.example.medilab.repository.RekamMedisRepository
import com.example.medilab.repository.RujukanRepository
import com.example.medilab.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MediLabApp : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var networkMonitor: NetworkMonitor
        private set
    lateinit var syncManager: SyncManager
        private set

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        networkMonitor = NetworkMonitor(this)
        syncManager = SyncManager(
            database = database,
            userRepository = UserRepository(),
            laporanRepository = LaporanRepository(),
            notifikasiRepository = NotifikasiRepository(),
            pemeriksaanRepository = PemeriksaanRepository(),
            obatRepository = ObatRepository(),
            dokterRepository = DokterRepository(),
            rekamMedisRepository = RekamMedisRepository(),
            rujukanRepository = RujukanRepository(),
            auditLogRepository = AuditLogRepository(),
            networkMonitor = networkMonitor
        )
        SyncWorker.schedule(this)
        syncManager.startListening()

        // Seed data from Firestore on first launch
        scope.launch {
            if (database.userDao().count() == 0) {
                syncManager.pullRemoteChanges()
            }
        }
    }

    companion object {
        lateinit var instance: MediLabApp
            private set
    }
}
