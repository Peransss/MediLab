package com.example.medilab.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medilab.database.dao.DokterDao
import com.example.medilab.database.dao.LaporanDao
import com.example.medilab.database.dao.NotifikasiDao
import com.example.medilab.database.dao.ObatDao
import com.example.medilab.database.dao.PemeriksaanDao
import com.example.medilab.database.dao.UserDao
import com.example.medilab.database.entity.DokterEntity
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.entity.NotifikasiEntity
import com.example.medilab.database.entity.ObatEntity
import com.example.medilab.database.entity.PemeriksaanEntity
import com.example.medilab.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, LaporanEntity::class, NotifikasiEntity::class, PemeriksaanEntity::class, ObatEntity::class, DokterEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun laporanDao(): LaporanDao
    abstract fun notifikasiDao(): NotifikasiDao
    abstract fun pemeriksaanDao(): PemeriksaanDao
    abstract fun obatDao(): ObatDao
    abstract fun dokterDao(): DokterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medilab_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
