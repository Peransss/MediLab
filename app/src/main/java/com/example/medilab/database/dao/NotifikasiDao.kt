package com.example.medilab.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.medilab.database.entity.NotifikasiEntity

@Dao
interface NotifikasiDao {
    @Query("SELECT * FROM notifikasi")
    fun getAll(): LiveData<List<NotifikasiEntity>>
}
