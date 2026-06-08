package com.example.medilab.database.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.medilab.MediLabApp
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as? MediLabApp ?: return Result.retry()
        val syncManager = app.syncManager
        return try {
            syncManager.pushPendingChanges()
            syncManager.pullRemoteChanges()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "medilab_sync"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES
            ).build()
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}
