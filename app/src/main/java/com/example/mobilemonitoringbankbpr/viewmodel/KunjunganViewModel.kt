package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.data.Kunjungan
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.data.SuratPeringatanListNasabahDropdown
import com.example.mobilemonitoringbankbpr.repository.KunjunganRepository
import com.example.mobilemonitoringbankbpr.repository.MonitoringRepository
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class KunjunganViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KunjunganRepository
    private val _nasabahList = MutableLiveData<List<Kunjungan>>()
    val nasabahList: LiveData<List<Kunjungan>> get() = _nasabahList
    private val _isSubmitting = MutableLiveData<Boolean>()
    val isSubmitting: LiveData<Boolean> get() = _isSubmitting

    private val _isSubmissionSuccessful = MutableLiveData<Boolean>()
    val isSubmissionSuccessful: LiveData<Boolean> get() = _isSubmissionSuccessful

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _submissionError = MutableLiveData<String?>()
    val submissionError: LiveData<String?> get() = _submissionError

    init {
        val apiService = RetrofitClient.getServiceWithAuth(application)
        repository = KunjunganRepository(apiService, application)
        Log.d("MonitoringViewModel", "ViewModel initialized with apiService")
    }
        private val _kunjunganList = MutableLiveData<List<Kunjungan>>()
        val kunjunganList: LiveData<List<Kunjungan>> get() = _kunjunganList

        fun fetchKunjungan(noNasabah: Long) {
            _isLoading.value = true
            viewModelScope.launch {
                try {
                    val result = repository.getKunjunganList(noNasabah)
                    _kunjunganList.value = result
                } catch (e: Exception) {
                    Log.e("", "Error fetching kunjungan: ", e)
                } finally {
                    _isLoading.value = false
                }
            }
        }
    fun fetchNasabahList() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nasabahs = repository.fetchNasabahList()
                _nasabahList.postValue(nasabahs)
                Log.d("KunjunganViewModel", "fetchNasabahList: Success")
            } catch (e: Exception) {
                Log.e("KunjunganViewModel", "fetchNasabahList: Error", e)
            } finally {
                _isLoading.postValue(false)
                Log.d("KunjunganViewModel", "fetchNasabahList completed")
            }
        }
    }
    fun submitKunjungan(
        kunjungan: Kunjungan,
        imageFile: File?,
    ) {
        _isSubmitting.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.submitKunjungan(
                kunjungan,
                imageFile,
                onSuccess = {
                    _isSubmitting.postValue(false)
                    _isSubmissionSuccessful.postValue(true)
                    _submissionError.postValue(null)
                    Log.d("KunjunganViewModel", "submitSuratPeringatan: Success")
                },
                onFailure = { error ->
                    _isSubmitting.postValue(false)
                    _isSubmissionSuccessful.postValue(false)
                    _submissionError.postValue(error.message)
                    Log.e("KunjunganViewModel", "submitSuratPeringatan: Error", error)
                }
            )
        }
    }
}




