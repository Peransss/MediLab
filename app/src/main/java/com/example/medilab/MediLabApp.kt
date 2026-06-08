package com.example.medilab

import android.app.Application
import com.example.medilab.database.AppDatabase

class MediLabApp : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
    }

    companion object {
        lateinit var instance: MediLabApp
            private set
    }
}
