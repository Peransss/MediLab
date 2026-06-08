package com.example.medilab.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.medilab.database.dao.AuditLogDao
import com.example.medilab.database.dao.DokterDao
import com.example.medilab.database.dao.LaporanDao
import com.example.medilab.database.dao.NotifikasiDao
import com.example.medilab.database.dao.ObatDao
import com.example.medilab.database.dao.PemeriksaanDao
import com.example.medilab.database.dao.RekamMedisDao
import com.example.medilab.database.dao.RujukanDao
import com.example.medilab.database.dao.UserDao
import com.example.medilab.database.entity.AuditLogEntity
import com.example.medilab.database.entity.DokterEntity
import com.example.medilab.database.entity.LaporanEntity
import com.example.medilab.database.entity.NotifikasiEntity
import com.example.medilab.database.entity.ObatEntity
import com.example.medilab.database.entity.PemeriksaanEntity
import com.example.medilab.database.entity.RekamMedisEntity
import com.example.medilab.database.entity.RujukanEntity
import com.example.medilab.database.entity.UserEntity

@Database(
    entities = [AuditLogEntity::class, DokterEntity::class, LaporanEntity::class, NotifikasiEntity::class, ObatEntity::class, PemeriksaanEntity::class, RekamMedisEntity::class, RujukanEntity::class, UserEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun auditLogDao(): AuditLogDao
    abstract fun dokterDao(): DokterDao
    abstract fun laporanDao(): LaporanDao
    abstract fun notifikasiDao(): NotifikasiDao
    abstract fun obatDao(): ObatDao
    abstract fun pemeriksaanDao(): PemeriksaanDao
    abstract fun rekamMedisDao(): RekamMedisDao
    abstract fun rujukanDao(): RujukanDao
    abstract fun userDao(): UserDao

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
