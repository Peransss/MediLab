package com.example.medilab.database

import androidx.room.TypeConverter
import com.example.medilab.model.HasilParameter
import com.example.medilab.model.ResepItem
import com.example.medilab.model.RumahSakit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromHasilParameterList(value: List<HasilParameter>): String = gson.toJson(value)

    @TypeConverter
    fun toHasilParameterList(value: String): List<HasilParameter> {
        val type = object : TypeToken<List<HasilParameter>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromResepItemList(value: List<ResepItem>): String = gson.toJson(value)

    @TypeConverter
    fun toResepItemList(value: String): List<ResepItem> {
        val type = object : TypeToken<List<ResepItem>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromRumahSakit(value: RumahSakit): String = gson.toJson(value)

    @TypeConverter
    fun toRumahSakit(value: String): RumahSakit {
        return gson.fromJson(value, RumahSakit::class.java) ?: RumahSakit()
    }

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String = value.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}
