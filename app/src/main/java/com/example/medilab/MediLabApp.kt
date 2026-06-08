package com.example.medilab

import android.app.Application
import com.example.medilab.database.AppDatabase
import com.example.medilab.database.sync.NetworkMonitor
import com.example.medilab.database.sync.SyncManager
import com.example.medilab.database.sync.SyncWorker
import com.example.medilab.model.User
import com.example.medilab.repository.AuditLogRepository
import com.example.medilab.repository.DokterRepository
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.repository.ObatRepository
import com.example.medilab.repository.PemeriksaanRepository
import com.example.medilab.repository.RekamMedisRepository
import com.example.medilab.repository.RujukanRepository
import com.example.medilab.repository.UserRepository
import com.example.medilab.util.Constants
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
                seedStaffAccounts()
            }
        }
    }

    private fun seedStaffAccounts() {
        val secondaryApp = try {
            FirebaseApp.getInstance("staffSeed")
        } catch (_: IllegalStateException) {
            val options = FirebaseApp.getInstance().options
            FirebaseApp.initializeApp(applicationContext, options, "staffSeed")
        }
        val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)
        val secondaryDb = FirebaseFirestore.getInstance(secondaryApp)

        data class StaffAccount(
            val email: String, val password: String,
            val nama: String, val role: String, val noHP: String
        )

        val accounts = listOf(
            StaffAccount("medilab.admin@gmail.com", "admin123", "Admin MediLab", Constants.ROLE_ADMIN, "081234567890"),
            StaffAccount("medilab.petugas@gmail.com", "petugas123", "Petugas MediLab", Constants.ROLE_PETUGAS, "081234567891"),
            StaffAccount("medilab.dokter@gmail.com", "dokter123", "Dokter MediLab", Constants.ROLE_DOKTER, "081234567892")
        )

        for (acc in accounts) {
            secondaryAuth.createUserWithEmailAndPassword(acc.email, acc.password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener
                    val user = User(
                        id = uid, nama = acc.nama, email = acc.email,
                        role = acc.role, noHP = acc.noHP
                    )
                    secondaryDb.collection(Constants.COLLECTION_USERS).document(uid).set(user)
                }
        }
    }

    companion object {
        lateinit var instance: MediLabApp
            private set
    }
}
