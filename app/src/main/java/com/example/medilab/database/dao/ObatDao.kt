package com.example.medilab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.ObatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObatDao {
    @Query("SELECT * FROM obat ORDER BY namaObat ASC")
    fun getAll(): Flow<List<ObatEntity>>

    @Query("SELECT * FROM obat WHERE id = :id")
    fun getById(id: String): Flow<ObatEntity?>

    @Query("SELECT * FROM obat WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSync(): List<ObatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ObatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<ObatEntity>)

    @Query("DELETE FROM obat WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE obat SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
