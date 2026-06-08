package com.example.medilab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.RujukanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RujukanDao {
    @Query("SELECT * FROM rujukan ORDER BY createdAt DESC")
    fun getAll(): Flow<List<RujukanEntity>>

    @Query("SELECT * FROM rujukan WHERE id = :id")
    fun getById(id: String): Flow<RujukanEntity?>

    @Query("SELECT * FROM rujukan WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSync(): List<RujukanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RujukanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<RujukanEntity>)

    @Query("DELETE FROM rujukan WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE rujukan SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
}
