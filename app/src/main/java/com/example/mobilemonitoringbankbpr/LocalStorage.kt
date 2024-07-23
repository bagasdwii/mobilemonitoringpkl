package com.example.mobilemonitoringbankbpr

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class LocalStorage(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("STORAGE_LOGIN_API", Context.MODE_PRIVATE)

    var userId: Int
        get() = sharedPreferences.getInt("user_id", -1)
        set(value) {
            Log.d("LocalStorage", "Saving userId: $value") // Log saat menyimpan userId
            sharedPreferences.edit().putInt("user_id", value).apply()
        }

    var token: String?
        get() = sharedPreferences.getString("TOKEN", "")
        set(value) {
            Log.d("LocalStorage", "Saving token: $value") // Log saat menyimpan token
            sharedPreferences.edit().putString("TOKEN", value).apply()
        }
    var jabatan: Int
        get() = sharedPreferences.getInt("jabatan", -1)
        set(value) {
            Log.d("LocalStorage", "Saving jabatan: $value") // Log saat menyimpan jabatan
            sharedPreferences.edit().putInt("jabatan", value).apply()
        }
}

