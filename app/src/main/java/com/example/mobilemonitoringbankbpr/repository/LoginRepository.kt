package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import com.example.mobilemonitoringbankbpr.data.Login
import com.example.mobilemonitoringbankbpr.data.ResponseLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository(private val context: Context) {

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

                        // Simpan userId dan token ke LocalStorage
                        val localStorage = LocalStorage(context)
                        localStorage.userId = userId
                        localStorage.token = token

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


