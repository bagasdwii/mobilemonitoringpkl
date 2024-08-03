package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.data.AdminKas
import com.example.mobilemonitoringbankbpr.data.AllDataResponse
import com.example.mobilemonitoringbankbpr.data.Cabang
import com.example.mobilemonitoringbankbpr.data.Direksi
import com.example.mobilemonitoringbankbpr.data.Jabatan
import com.example.mobilemonitoringbankbpr.data.KepalaCabang
import com.example.mobilemonitoringbankbpr.data.Supervisor
import com.example.mobilemonitoringbankbpr.data.UpdateUser
import com.example.mobilemonitoringbankbpr.data.UpdateUserResponse
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.data.Wilayah
import com.example.mobilemonitoringbankbpr.repository.AdminRepository
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AdminRepository

    private val _user = MutableLiveData<List<User>>()
    val user: LiveData<List<User>> get() = _user
    private val _cabang = MutableLiveData<List<Cabang>>()
    val cabang: LiveData<List<Cabang>> get() = _cabang
    private val _wilayah = MutableLiveData<List<Wilayah>>()
    val wilayah: LiveData<List<Wilayah>> get() = _wilayah

    private val _jabatan = MutableLiveData<List<Jabatan>>()
    val jabatan: LiveData<List<Jabatan>> get() = _jabatan

    private val _direksi = MutableLiveData<List<Direksi>>()
    val direksi: LiveData<List<Direksi>> get() = _direksi

    private val _kepalacabang = MutableLiveData<List<KepalaCabang>>()
    val kepalacabang: LiveData<List<KepalaCabang>> get() = _kepalacabang
    private val _supervisor = MutableLiveData<List<Supervisor>>()
    val supervisor: LiveData<List<Supervisor>> get() = _supervisor

    val adminkas: LiveData<List<AdminKas>> get() = _adminkas
    private val _adminkas = MutableLiveData<List<AdminKas>>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private var currentPage = 1
    private val _allData = MutableLiveData<AllDataResponse>()
    val allData: LiveData<AllDataResponse> get() = _allData
    private val _updateUserResult = MutableLiveData<Result<UpdateUserResponse>>()
    val updateUserResult: LiveData<Result<UpdateUserResponse>> get() = _updateUserResult

    init {
        val apiService = RetrofitClient.getServiceWithAuth(application)
        repository = AdminRepository(apiService,application)
        Log.d("AdminViewModel", "ViewModel initialized with apiService")
    }

//    fun updateUser(userId: Int, updateUser: UpdateUser) {
//        viewModelScope.launch {
//            val result = repository.updateUser(userId, updateUser)
//            _updateUserResult.postValue(result)
//        }
//    }
    fun updateUser(userId: Int, updateUser: UpdateUser) {
        repository.updateUser(userId, updateUser) { result ->
            _updateUserResult.postValue(result)
        }
    }
    fun fetchAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            _allData.value = repository.fetchAllData()
            _isLoading.value = false
        }
    }
    fun getUser(searchQuery: String) {
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