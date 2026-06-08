package com.example.medilab.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.medilab.database.entity.LaporanEntity

@Dao
interface LaporanDao {
    @Query("SELECT * FROM laporans")
    fun getAll(): LiveData<List<LaporanEntity>>
}
