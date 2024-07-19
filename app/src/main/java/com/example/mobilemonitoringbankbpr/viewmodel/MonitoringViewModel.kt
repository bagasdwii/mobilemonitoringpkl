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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder

class MonitoringViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MonitoringRepository(application)

    private val _nasabahs = MutableLiveData<List<Nasabah>>()
    val nasabahs: LiveData<List<Nasabah>> get() = _nasabahs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var currentPage = 1

    fun getNasabahs(searchQuery: String) {
        _isLoading.value = true
        Log.d("MonitoringViewModel", "Fetching nasabahs started with search query: $searchQuery on page: $currentPage")
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getNasabahs(searchQuery, currentPage)
            withContext(Dispatchers.Main) {
                result.onSuccess {
                    _nasabahs.value = it
                    Log.d("MonitoringViewModel", "Nasabahs fetched successfully: ${it.size} items")
                }.onFailure {
                    _errorMessage.value = it.message
                    Log.e("MonitoringViewModel", "Error fetching nasabahs", it)
                }
                _isLoading.value = false
                Log.d("MonitoringViewModel", "Fetching nasabahs completed")
            }
        }
    }

    fun setPage(page: Int) {
        currentPage = page
    }

    fun getCurrentPage(): Int {
        return currentPage
    }
}






