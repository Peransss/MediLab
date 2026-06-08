package com.example.medilab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.RekamMedisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RekamMedisDao {
    @Query("SELECT * FROM rekam_medis ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RekamMedisEntity>>

    @Query("SELECT * FROM rekam_medis WHERE id = :id")
    fun getById(id: String): Flow<RekamMedisEntity?>

    @Query("SELECT * FROM rekam_medis WHERE pasienId = :pasienId ORDER BY createdAt DESC")
    fun getByPasienId(pasienId: String): Flow<List<RekamMedisEntity>>

    @Query("SELECT * FROM rekam_medis WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSync(): List<RekamMedisEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RekamMedisEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<RekamMedisEntity>)

    @Query("DELETE FROM rekam_medis WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE rekam_medis SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
