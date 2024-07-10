package com.example.mobilemonitoringbankbpr.repository

import android.util.Log
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.server.ApiService

class MonitoringRepository(private val apiService: ApiService) {

    suspend fun getNasabahs(searchQuery: String): List<Nasabah>? {
        return try {
            val response = apiService.getNasabahs(searchQuery)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MonitoringRepository", "Error fetching nasabahs, status code: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MonitoringRepository", "Exception while fetching nasabahs", e)
            null
        }
    }

    suspend fun getSuratPeringatan(nasabahNo: Long, tingkat: Int): SuratPeringatan? {
        return try {
            val response = apiService.getSuratPeringatan(nasabahNo, tingkat)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MonitoringRepository", "Error fetching Surat Peringatan, status code: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MonitoringRepository", "Exception while fetching Surat Peringatan", e)
            null
        }
    }

    suspend fun checkSuratPeringatan(nasabahNo: Long): SuratPeringatan? {
        return try {
            val response = apiService.checkSuratPeringatan(nasabahNo)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MonitoringRepository", "Error checking Surat Peringatan, status code: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MonitoringRepository", "Exception while checking Surat Peringatan", e)
            null
        }
    }
}

