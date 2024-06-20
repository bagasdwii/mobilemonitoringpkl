package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobilemonitoringbankbpr.UserRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application)

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> get() = _loginResult

    fun login(email: String, password: String) {
        userRepository.login(email, password) { result ->
            _loginResult.postValue(result)
            result.onFailure { exception ->
                Log.e("LOGIN_VIEW_MODEL", "Login failed: ${exception.message}")
            }
        }
    }
}

