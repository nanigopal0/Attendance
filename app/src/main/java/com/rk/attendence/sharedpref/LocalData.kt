package com.rk.attendence.sharedpref

import android.content.Context
import android.content.SharedPreferences

object LocalData {
    private const val PREFS_NAME = "UserInfo"
    private lateinit var sharedPreferences: SharedPreferences
    private var isInitialized = false
    const val CLASS_ID = "class_id"
    const val SEMESTER_ID = "semester_id"
    const val CURRENT_SEMESTER_ID = "current_semester_id"
    const val NAME = "name"
    const val DATE_RANGE_PICKER_START_DATE = "start_date"
    const val DATE_RANGE_PICKER_END_DATE = "end_date"
    const val BROADCAST_CLASS = "broadcast_today_class"
    const val TODAY_CLASS = "today_class"
    const val NOTIFICATION = "notification"
    const val ALREADY_INSTALLED = "already_installed"

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isInitialized = true
    }

    fun deleteData() {
        sharedPreferences.edit().clear().apply()
    }

    fun removeKey(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun setInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun setLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }
}