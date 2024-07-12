package com.example.mobilemonitoringbankbpr.repository

import android.app.Application
import android.util.Log
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import com.example.mobilemonitoringbankbpr.data.Jabatan
import com.example.mobilemonitoringbankbpr.data.Register
import com.example.mobilemonitoringbankbpr.data.ResponseRegister
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository(private val application: Application) {

    private val apiServiceWithoutAuth = RetrofitClient.getServiceWithoutAuth()

    fun fetchJabatanData(callback: (Result<List<Jabatan>>) -> Unit) {
        apiServiceWithoutAuth.getJabatanData().enqueue(object : Callback<List<Jabatan>> {
            override fun onResponse(call: Call<List<Jabatan>>, response: Response<List<Jabatan>>) {
                if (response.isSuccessful) {
                    val jabatanList = response.body() ?: emptyList()
                    Log.d("RepositoryRegister", "Received Jabatan data: $jabatanList")
                    callback(Result.success(jabatanList))
                } else {
                    Log.e("RepositoryRegister", "HTTP error: ${response.code()}")
                    callback(Result.failure(Exception("HTTP error: ${response.code()}")))
                }
            }

            override fun onFailure(call: Call<List<Jabatan>>, t: Throwable) {
                Log.e("RepositoryRegister", "Error fetching Jabatan data", t)
                callback(Result.failure(t))
            }
        })
    }

    fun registerUser(registerRequest: Register, callback: (Result<String>) -> Unit) {
        Log.d("RepositoryRegister", "Request: $registerRequest")

        apiServiceWithoutAuth.registerUser(registerRequest).enqueue(object : Callback<ResponseRegister> {
            override fun onResponse(call: Call<ResponseRegister>, response: Response<ResponseRegister>) {
                Log.d("RepositoryRegister", "Response Code: ${response.code()}")
                Log.d("RepositoryRegister", "Response Body: ${response.body()}")
                val errorBodyString = response.errorBody()?.string()
                Log.d("RepositoryRegister", "Error Body: $errorBodyString")

                if (response.isSuccessful) {
                    response.body()?.let {
                        callback(Result.success(it.message))
                    } ?: callback(Result.failure(Exception("Unknown error")))
                } else {
                    val errorMessage = if (!errorBodyString.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBodyString)
                            val message = jsonObject.optString("message", "Unknown error")
                            val errors = jsonObject.optJSONObject("errors")
                            if (errors != null) {
                                val errorMessages = StringBuilder(message)
                                errors.keys().forEach { key ->
                                    val messagesArray = errors.optJSONArray(key)
                                    if (messagesArray != null) {
                                        for (i in 0 until messagesArray.length()) {
                                            errorMessages.append("\n").append(messagesArray.optString(i))
                                        }
                                    }
                                }
                                errorMessages.toString()
                            } else {
                                message
                            }
                        } catch (e: JSONException) {
                            Log.e("RepositoryRegister", "Failed to parse error JSON", e)
                            "Failed to parse server error response"
                        }
                    } else {
                        "Unknown error"
                    }
                    Log.d("RepositoryRegister", "Error Message: $errorMessage")
                    callback(Result.failure(Exception(errorMessage)))
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                Log.e("RepositoryRegister", "Failed to register user", t)
                callback(Result.failure(t))
            }
        })
    }







}

