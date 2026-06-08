package com.example.medilab

import android.app.Application
import com.example.medilab.database.AppDatabase
import com.example.medilab.database.sync.NetworkMonitor
import com.example.medilab.database.sync.SyncManager
import com.example.medilab.database.sync.SyncWorker
import com.example.medilab.repository.LaporanRepository
import com.example.medilab.repository.NotifikasiRepository
import com.example.medilab.repository.UserRepository

class MediLabApp : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var networkMonitor: NetworkMonitor
        private set
    lateinit var syncManager: SyncManager
        private set

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
            networkMonitor = networkMonitor
        )
        SyncWorker.schedule(this)
        syncManager.startListening()
    }

    companion object {
        lateinit var instance: MediLabApp
            private set
    }
}
