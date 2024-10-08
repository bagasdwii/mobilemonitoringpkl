package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import android.util.Log
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Cabang
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.server.ApiService
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import org.json.JSONObject
import java.net.URLEncoder

class MonitoringRepository(private val apiService: ApiService, private val context: Context) {

//    suspend fun getNasabahs(searchQuery: String, page: Int): Result<List<Nasabah>> {
//        return try {
//            Log.d("MonitoringRepository", "Fetching nasabahs with query: $searchQuery, page: $page")
//            val response = apiService.getNasabahs(searchQuery, page)
//            if (response.isSuccessful) {
//                response.body()?.let {
//                    Log.d("MonitoringRepository", "Nasabahs fetched successfully")
//                    Result.success(it.data)
//                } ?: run {
//                    Log.e("MonitoringRepository", "Error parsing response")
//                    Result.failure(Exception("Error parsing response"))
//                }
//            } else {
//                Log.e("MonitoringRepository", "Error fetching nasabahs, status code: ${response.code()}")
//                Result.failure(Exception("Error fetching nasabahs, status code: ${response.code()}"))
//            }
//        } catch (e: Exception) {
//            Log.e("MonitoringRepository", "Exception occurred: ${e.message}", e)
//            Result.failure(e)
//        }
//    }

    suspend fun getNasabahs(searchQuery: String, cabang: String, page: Int): Result<List<Nasabah>> {
        return try {
            Log.d("MonitoringRepository", "Fetching nasabahs with query: $searchQuery, cabang: $cabang, page: $page")
            val response = apiService.getNasabahs(searchQuery, cabang, page)
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


    suspend fun getCabangList(): List<Cabang> {
        Log.d("AdminRepository", "fetchCabangList: Start")

        val apiService = RetrofitClient.getServiceWithAuth(context)

        return try {
            val response = apiService.getCabangList()
            Log.d("AdminRepository", "fetchCabangList: Success")
            response
        } catch (e: Exception) {
            Log.e("AdminRepository", "fetchCabangList: Error fetching data", e)
            emptyList()
        }
    }
//    suspend fun getGambarUrl(filename: String): Result<String> {
//        return try {
//            val response = apiService.getGambarUrl(filename)
//            if (response.isSuccessful) {
//                Result.success(response.body() ?: "")
//            } else {
//                Result.failure(Exception("Failed to get image URL: ${response.errorBody()?.string()}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun getPdfUrl(filename: String): Result<String> {
//        return try {
//            val response = apiService.getPdfUrl(filename)
//            if (response.isSuccessful) {
//                Result.success(response.body() ?: "")
//            } else {
//                Result.failure(Exception("Failed to get PDF URL: ${response.errorBody()?.string()}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
}



