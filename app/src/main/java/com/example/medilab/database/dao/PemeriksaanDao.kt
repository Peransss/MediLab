package com.example.medilab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.PemeriksaanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PemeriksaanDao {
    @Query("SELECT * FROM pemeriksaan ORDER BY namaPemeriksaan ASC")
    fun getAll(): Flow<List<PemeriksaanEntity>>

    @Query("SELECT * FROM pemeriksaan WHERE id = :id")
    fun getById(id: String): Flow<PemeriksaanEntity?>

    @Query("SELECT * FROM pemeriksaan WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSync(): List<PemeriksaanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PemeriksaanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<PemeriksaanEntity>)

    @Query("DELETE FROM pemeriksaan WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE pemeriksaan SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
