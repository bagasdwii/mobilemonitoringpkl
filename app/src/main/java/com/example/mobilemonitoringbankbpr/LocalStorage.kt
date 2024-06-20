package com.example.mobilemonitoringbankbpr

import android.content.Context
import android.content.SharedPreferences

class LocalStorage (private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("STORAGE_LOGIN_API", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    var token: String?
        get() = sharedPreferences.getString("TOKEN", "")
        set(value) {
            editor.putString("TOKEN", value)
            editor.commit()
        }
}
