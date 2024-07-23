package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.data.ConnectionResponse
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import com.example.mobilemonitoringbankbpr.data.Login
import com.example.mobilemonitoringbankbpr.data.ResponseLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(private val context: Context) {
    fun checkConnection(callback: (Result<Boolean>) -> Unit) {
        val apiService = RetrofitClient.getServiceWithoutAuth()
        apiService.checkConnection().enqueue(object : Callback<ConnectionResponse> {
            override fun onResponse(call: Call<ConnectionResponse>, response: Response<ConnectionResponse>) {
                if (response.isSuccessful) {
                    callback(Result.success(true))
                } else {
                    callback(Result.failure(Exception("Server connection failed: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<ConnectionResponse>, t: Throwable) {
                callback(Result.failure(Exception("Gagal Terkoneksi Dengan Server")))
            }
        })
    }
    fun login(email: String, password: String, callback: (Result<String>) -> Unit) {
        val apiService = RetrofitClient.getServiceWithoutAuth()

        val loginRequest = Login(email, password)
        apiService.loginUser(loginRequest).enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val token = responseBody.token
                        val userId = responseBody.user_id
                        val jabatan = responseBody.jabatan_id
                        val name = responseBody.name

                        // Simpan userId dan token ke LocalStorage
                        val localStorage = LocalStorage(context)
                        localStorage.userId = userId
                        localStorage.token = token
                        localStorage.jabatan = jabatan
                        localStorage.name = name

                        callback(Result.success(token))
                    } else {
                        callback(Result.failure(Exception("Response body is null")))
                    }
                } else {
                    callback(Result.failure(Exception("Login failed: ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                callback(Result.failure(Exception("Login failed: ${t.message}")))
            }
        })
    }
}


