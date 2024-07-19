package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import android.util.Log
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.server.ApiService
import org.json.JSONObject
import java.net.URLEncoder

class MonitoringRepository(private val apiService: ApiService) {

    suspend fun getNasabahs(searchQuery: String, page: Int): Result<List<Nasabah>> {
        return try {
            Log.d("MonitoringRepository", "Fetching nasabahs with query: $searchQuery, page: $page")
            val response = apiService.getNasabahs(searchQuery, page)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("MonitoringRepository", "Nasabahs fetched successfully")
                    Result.success(it.data)
                } ?: run {
                    Log.e("MonitoringRepository", "Error parsing response")
                    Result.failure(Exception("Error parsing response"))
                }
            } else {
                Log.e("MonitoringRepository", "Error fetching nasabahs, status code: ${response.code()}")
                Result.failure(Exception("Error fetching nasabahs, status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("MonitoringRepository", "Exception occurred: ${e.message}", e)
            Result.failure(e)
        }
    }

}



