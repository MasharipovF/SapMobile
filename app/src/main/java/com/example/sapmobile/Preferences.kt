package com.example.sapmobile

import android.content.Context
import android.content.SharedPreferences

object Preferences {
    const val CREDENTIALS = "CREDENTIALS"


    fun init(context: Context) {
        preferences = context.getSharedPreferences(CREDENTIALS, Context.MODE_PRIVATE)
    }

    fun setPreference(preferences: SharedPreferences) {
        Preferences.preferences = preferences
    }

    lateinit var preferences: SharedPreferences


    var sessionID: String?
        get() = preferences.getString(Preferences::sessionID.name, "NOTHING")
        set(value) {
            preferences.edit().putString(Preferences::sessionID.name, value).apply()
        }

    var companyDB: String?
        get() = preferences.getString(Preferences::companyDB.name, "NOTHING")
        set(value) {
            preferences.edit().putString(Preferences::companyDB.name, value).apply()
        }

}