package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobilemonitoringbankbpr.data.AdminKas
import com.example.mobilemonitoringbankbpr.data.AllDataResponse
import com.example.mobilemonitoringbankbpr.data.Cabang
import com.example.mobilemonitoringbankbpr.data.Direksi
import com.example.mobilemonitoringbankbpr.data.Jabatan
import com.example.mobilemonitoringbankbpr.data.KepalaCabang
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.NasabahSp
import com.example.mobilemonitoringbankbpr.data.Supervisor
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.data.Wilayah
import com.example.mobilemonitoringbankbpr.server.ApiService
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminRepository(private val apiService: ApiService, private val context: Context){

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
    suspend fun fetchAllData(): AllDataResponse? {
        return try {
            val response = apiService.getAllData()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    suspend fun fetchCabangList(): List<Cabang> {
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
    suspend fun fetchWilayahList(): List<Wilayah> {
        Log.d("AdminRepository", "fetchWilayahList: Start")

        val apiService = RetrofitClient.getServiceWithAuth(context)

        return try {
            val response = apiService.getWilayahList()
            Log.d("AdminRepository", "fetchWilayahList: Success")
            response
        } catch (e: Exception) {
            Log.e("AdminRepository", "fetchWilayahList: Error fetching data", e)
            emptyList()
        }
    }
    suspend fun fetchJabatanList(): List<Jabatan> {
        Log.d("AdminRepository", "fetchJabatanList: Start")

        val apiService = RetrofitClient.getServiceWithAuth(context)

        return try {
            val response = apiService.getJabatanList()
            Log.d("AdminRepository", "fetchJabatanList: Success")
            response
        } catch (e: Exception) {
            Log.e("AdminRepository", "fetchJabatanList: Error fetching data", e)
            emptyList()
        }
    }
    suspend fun fetchDireksiList(): List<Direksi> {
        Log.d("AdminRepository", "fetchDireksiList: Start")

        val apiService = RetrofitClient.getServiceWithAuth(context)

        return try {
            val response = apiService.getDireksiList()
            Log.d("AdminRepository", "fetchDireksiList: Success")
            response
        } catch (e: Exception) {
            Log.e("AdminRepository", "fetchDireksiList: Error fetching data", e)
            emptyList()
        }
    }
    suspend fun fetchSupervisorList(): List<Supervisor> {
        Log.d("AdminRepository", "fetchSupervisorList: Start")

        val apiService = RetrofitClient.getServiceWithAuth(context)

        return try {
            val response = apiService.getSupervisorList()
            Log.d("AdminRepository", "fetchSupervisorList: Success")
            response
        } catch (e: Exception) {
            Log.e("AdminRepository", "fetchSupervisorList: Error fetching data", e)
            emptyList()
        }
    }
    suspend fun fetchAdminKasList(): List<AdminKas> {
        Log.d("AdminRepository", "fetchAdminKasList: Start")

        val apiService = RetrofitClient.getServiceWithAuth(context)

        return try {
            val response = apiService.getAdminkasList()
            Log.d("AdminRepository", "fetchAdminKasList: Success")
            response
        } catch (e: Exception) {
            Log.e("AdminRepository", "fetchAdminKasList: Error fetching data", e)
            emptyList()
        }
    }
    suspend fun fetchKepalaCabangList(): List<KepalaCabang> {
        Log.d("AdminRepository", "fetchKepalaCabangList: Start")

        val apiService = RetrofitClient.getServiceWithAuth(context)

        return try {
            val response = apiService.getKepalacabangList()
            Log.d("AdminRepository", "fetchKepalaCabangList: Success")
            response
        } catch (e: Exception) {
            Log.e("AdminRepository", "fetchKepalaCabangList: Error fetching data", e)
            emptyList()
        }
    }
}