package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import com.example.mobilemonitoringbankbpr.data.Kunjungan
import com.example.mobilemonitoringbankbpr.server.ApiService

class KunjunganRepository(private val apiService: ApiService,  private val context: Context) {

    suspend fun getKunjunganList(noNasabah: Long): List<Kunjungan> {
        return apiService.getKunjunganList(noNasabah)
    }
}
