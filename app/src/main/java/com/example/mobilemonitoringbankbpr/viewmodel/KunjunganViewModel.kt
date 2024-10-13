package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.data.Kunjungan
import com.example.mobilemonitoringbankbpr.repository.KunjunganRepository
import com.example.mobilemonitoringbankbpr.repository.MonitoringRepository
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import kotlinx.coroutines.launch

class KunjunganViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KunjunganRepository

    init {
        val apiService = RetrofitClient.getServiceWithAuth(application)
        repository = KunjunganRepository(apiService, application)
        Log.d("MonitoringViewModel", "ViewModel initialized with apiService")
    }

    private val _kunjunganList = MutableLiveData<List<Kunjungan>>()
    val kunjunganList: LiveData<List<Kunjungan>> get() = _kunjunganList

    fun fetchKunjungan(noNasabah: Long) {
        viewModelScope.launch {
            try {
                val response = repository.getKunjunganList(noNasabah)
                _kunjunganList.value = response
            } catch (e: Exception) {
                Log.e("KunjunganViewModel", "Error fetching kunjungan data: ${e.message}")
            }
        }
    }
}

