package com.example.medilab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.DokterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DokterDao {
    @Query("SELECT * FROM dokter ORDER BY nama ASC")
    fun getAll(): Flow<List<DokterEntity>>

    @Query("SELECT * FROM dokter WHERE id = :id")
    fun getById(id: String): Flow<DokterEntity?>

    @Query("SELECT * FROM dokter WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSync(): List<DokterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DokterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<DokterEntity>)

    @Query("DELETE FROM dokter WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE dokter SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
