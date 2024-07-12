package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.repository.AccountRepository

class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.getServiceWithAuth(application.applicationContext)
    private val repository = AccountRepository(apiService)

    val user = MutableLiveData<User>()
    val isLoading = MutableLiveData<Boolean>()
    val isConnected = MutableLiveData<Boolean>()

    init {
        checkConnection()
    }

    private fun checkConnection() {
        isLoading.value = true
        repository.checkConnection(getApplication()) { connected ->
            isConnected.value = connected
            if (connected) {
                getUser()
            } else {
                isLoading.value = false
                logoutLocally()
                Log.e("AccountViewModel", "No internet connection")
            }
        }
    }

    private fun getUser() {
        Log.d("AccountViewModel", "Fetching user data...")
        repository.getUser().observeForever { userData ->
            if (userData != null) {
                user.value = userData
                Log.d("AccountViewModel", "User data fetched: ${userData.name}")
            } else {
                Log.e("AccountViewModel", "Failed to fetch user data")
            }
            isLoading.value = false
        }
    }

    fun logout(onResult: (Boolean) -> Unit) {
        isLoading.value = true
        repository.logout(getApplication()) { success ->
            isLoading.value = false
            onResult(success)
        }
    }

    fun logoutLocally() {
        repository.logoutLocally(getApplication())
    }
}












