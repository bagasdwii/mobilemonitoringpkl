package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobilemonitoringbankbpr.repository.LoginRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = LoginRepository(application)

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> get() = _loginResult
    fun checkConnectionAndLogin(email: String, password: String) {
        userRepository.checkConnection { result ->
            result.onSuccess {
                // Jika koneksi berhasil, lanjutkan login
                login(email, password)
            }.onFailure { exception ->
                // Jika koneksi gagal, laporkan kesalahan
                _loginResult.postValue(Result.failure(Exception("${exception.message}")))
            }
        }
    }
    fun login(email: String, password: String) {
        userRepository.login(email, password) { result ->
            _loginResult.postValue(result)

        }
    }
    fun checkConnection(callback: (Result<Boolean>) -> Unit) {
        userRepository.checkConnection(callback)
    }
}

