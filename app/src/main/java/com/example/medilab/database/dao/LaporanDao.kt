package com.example.medilab.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medilab.database.SyncStatus
import com.example.medilab.database.entity.LaporanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LaporanDao {
    @Query("SELECT * FROM laporan ORDER BY createdAt DESC")
    fun getAll(): Flow<List<LaporanEntity>>

    @Query("SELECT * FROM laporan WHERE id = :id")
    fun getById(id: String): Flow<LaporanEntity?>

    @Query("SELECT * FROM laporan WHERE status = :status ORDER BY createdAt DESC")
    fun getByStatus(status: String): Flow<List<LaporanEntity>>

    @Query("SELECT * FROM laporan WHERE pasienId = :pasienId ORDER BY createdAt DESC")
    fun getByPasienId(pasienId: String): Flow<List<LaporanEntity>>

    @Query("SELECT * FROM laporan WHERE pasienId = :pasienId AND status = :status ORDER BY createdAt DESC")
    fun getByPasienIdAndStatus(pasienId: String, status: String): Flow<List<LaporanEntity>>

    @Query("SELECT * FROM laporan WHERE syncStatus != 'SYNCED'")
    suspend fun getPendingSync(): List<LaporanEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(laporan: LaporanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(laporans: List<LaporanEntity>)

    @Query("DELETE FROM laporan WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE laporan SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus)

    @Query("SELECT COUNT(*) FROM laporan")
    suspend fun count(): Int
}
