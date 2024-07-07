package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.data.Jabatan
import com.example.mobilemonitoringbankbpr.data.Register
import com.example.mobilemonitoringbankbpr.repository.RepositoryRegister

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RepositoryRegister(application)

    private val _jabatanList = MutableLiveData<List<Jabatan>>()
    val jabatanList: LiveData<List<Jabatan>> get() = _jabatanList

    fun fetchJabatanData() {
        repository.fetchJabatanData { result ->
            result.onSuccess { jabatanList ->
                Log.d("RegisterViewModel", "Fetched Jabatan list: $jabatanList")
                _jabatanList.postValue(jabatanList)
            }.onFailure { error ->
                Log.e("RegisterViewModel", "Error fetching jabatan data", error)
            }
        }
    }

    fun registerUser(registerRequest: Register, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repository.registerUser(registerRequest) { result ->
            result.onSuccess {
                onSuccess()
            }.onFailure { error ->
                onError(error.message ?: "Unknown error")
            }
        }
    }
}

