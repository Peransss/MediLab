package com.example.medilab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.NotifikasiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotifikasiDao {
    @Query("SELECT * FROM notifikasi WHERE userId = :userId ORDER BY createdAt DESC")
    fun getByUserId(userId: String): Flow<List<NotifikasiEntity>>

    @Query("SELECT * FROM notifikasi WHERE id = :id")
    fun getById(id: String): Flow<NotifikasiEntity?>

    @Query("SELECT * FROM notifikasi WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSync(): List<NotifikasiEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(notifikasi: NotifikasiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(notifikasis: List<NotifikasiEntity>)

    @Query("UPDATE notifikasi SET dibaca = 1, syncStatus = 'PENDING_UPDATE' WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifikasi SET dibaca = 1, syncStatus = 'PENDING_UPDATE' WHERE userId = :userId AND dibaca = 0")
    suspend fun markAllAsRead(userId: String)

    @Query("DELETE FROM notifikasi WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE notifikasi SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
