package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.server.ApiService
import com.example.mobilemonitoringbankbpr.data.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountRepository(private val apiService: ApiService) {

    fun getUser(): LiveData<User?> {
        val data = MutableLiveData<User?>()

        apiService.getUser().enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Log.d("AccountRepository", "User fetched successfully: ${response.body()}")
                    data.value = response.body()
                } else {
                    Log.e("AccountRepository", "Failed to fetch user: ${response.errorBody()?.string()}")
                    data.value = null
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("AccountRepository", "Error fetching user", t)
                data.value = null
            }
        })

        return data
    }

    fun logout(context: Context, onResult: (Boolean) -> Unit) {
        val url = context.getString(R.string.api_server) + "/logoutmobile"
        Log.d("AccountRepository", "Starting logout process")
        apiService.logoutUser().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("AccountRepository", "Logout successful")
                    val localStorage = LocalStorage(context)
                    localStorage.token = null
                    localStorage.userId = -1
                    onResult(true)
                } else {
                    Log.e("AccountRepository", "Logout failed with code: ${response.code()}")
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("AccountRepository", "Error during logout", t)
                onResult(false)
            }
        })
    }
}

