package com.example.medilab

import android.app.Application

class MediLabApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MediLabApp
            private set
    }
}
