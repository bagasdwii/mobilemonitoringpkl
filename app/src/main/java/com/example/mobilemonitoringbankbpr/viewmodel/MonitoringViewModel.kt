package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.repository.MonitoringRepository
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder

class MonitoringViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MonitoringRepository

    private val _nasabahs = MutableLiveData<List<Nasabah>>()
    val nasabahs: LiveData<List<Nasabah>> get() = _nasabahs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var currentPage = 1

    init {
        val apiService = RetrofitClient.getServiceWithAuth(application)
        repository = MonitoringRepository(apiService)
        Log.d("MonitoringViewModel", "ViewModel initialized with apiService")
    }

    fun getNasabahs(searchQuery: String) {
        _isLoading.value = true
        viewModelScope.launch {
            Log.d("MonitoringViewModel", "Getting nasabahs for query: $searchQuery, page: $currentPage")
            val result = repository.getNasabahs(searchQuery, currentPage)
            result.onSuccess {
                Log.d("MonitoringViewModel", "Nasabahs retrieved successfully: $it")
                _nasabahs.postValue(it)
            }.onFailure {
                Log.e("MonitoringViewModel", "Failed to retrieve nasabahs: ${it.message}")
                _errorMessage.postValue(it.message)
            }
            _isLoading.postValue(false)
        }
    }

    fun setPage(page: Int) {
        currentPage = page
        Log.d("MonitoringViewModel", "Page set to: $currentPage")
    }

    fun getCurrentPage(): Int {
        Log.d("MonitoringViewModel", "Current page: $currentPage")
        return currentPage
    }

//    suspend fun getGambarUrl(filename: String): String? {
//        return try {
//            val result = repository.getGambarUrl(filename)
//            result.getOrNull()
//        } catch (e: Exception) {
//            _errorMessage.postValue(e.message)
//            null
//        }
//    }
//
//    suspend fun getPdfUrl(filename: String): String? {
//        return try {
//            val result = repository.getPdfUrl(filename)
//            result.getOrNull()
//        } catch (e: Exception) {
//            _errorMessage.postValue(e.message)
//            null
//        }
//    }
}









