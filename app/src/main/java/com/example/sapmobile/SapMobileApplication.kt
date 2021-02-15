package com.example.sapmobile

import android.app.Application

class SapMobileApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Preferences.init(this)
    }
}