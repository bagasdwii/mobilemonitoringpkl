package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.repository.AdminRepository
import com.example.mobilemonitoringbankbpr.repository.MonitoringRepository
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AdminRepository

    private val _user = MutableLiveData<List<User>>()
    val user: LiveData<List<User>> get() = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var currentPage = 1

    init {
        val apiService = RetrofitClient.getServiceWithAuth(application)
        repository = AdminRepository(apiService)
        Log.d("AdminViewModel", "ViewModel initialized with apiService")
    }

    fun getNasabahs(searchQuery: String) {
        _isLoading.value = true
        viewModelScope.launch {
            Log.d("AdminViewModel", "Getting user for query: $searchQuery, page: $currentPage")
            val result = repository.getUser(searchQuery, currentPage)
            result.onSuccess {
                Log.d("AdminViewModel", "user retrieved successfully: $it")
                _user.postValue(it)
            }.onFailure {
                Log.e("AdminViewModel", "Failed to retrieve user: ${it.message}")
                _errorMessage.postValue(it.message)
            }
            _isLoading.postValue(false)
        }
    }

    fun setPage(page: Int) {
        currentPage = page
        Log.d("AdminViewModel", "Page set to: $currentPage")
    }

    fun getCurrentPage(): Int {
        Log.d("AdminViewModel", "Current page: $currentPage")
        return currentPage
    }
}