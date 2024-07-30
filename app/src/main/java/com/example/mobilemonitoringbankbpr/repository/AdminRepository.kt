package com.example.mobilemonitoringbankbpr.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.server.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminRepository(private val apiService: ApiService) {

    suspend fun getUser(searchQuery: String, page: Int): Result<List<User>> {
        return try {
            Log.d("AdminRepository", "Fetching User with query: $searchQuery, page: $page")
            val response = apiService.getUser(searchQuery, page)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("AdminRepository", "User fetched successfully")
                    Result.success(it.data)
                } ?: run {
                    Log.e("AdminRepository", "Error parsing response")
                    Result.failure(Exception("Error parsing response"))
                }
            } else {
                Log.e("AdminRepository", "Error fetching User, status code: ${response.code()}")
                Result.failure(Exception("Error fetching User, status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("AdminRepository", "Exception occurred: ${e.message}", e)
            Result.failure(e)
        }
    }

}