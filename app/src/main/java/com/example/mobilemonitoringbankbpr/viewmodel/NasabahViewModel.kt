package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.NasabahRepository
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.NasabahSp
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class NasabahViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NasabahRepository(application)

    private val _nasabahList = MutableLiveData<List<NasabahSp>>()
    val nasabahList: LiveData<List<NasabahSp>> get() = _nasabahList

    private val _isSubmitting = MutableLiveData<Boolean>()
    val isSubmitting: LiveData<Boolean> get() = _isSubmitting

    private val _isSubmissionSuccessful = MutableLiveData<Boolean>()
    val isSubmissionSuccessful: LiveData<Boolean> get() = _isSubmissionSuccessful

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchNasabahList() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val nasabahs = repository.fetchNasabahList()
                _nasabahList.postValue(nasabahs)
                Log.d("NasabahViewModel", "fetchNasabahList: Success")
            } catch (e: Exception) {
                Log.e("NasabahViewModel", "fetchNasabahList: Error", e)
            } finally {
                _isLoading.postValue(false)
                Log.d("NasabahViewModel", "fetchNasabahList completed")
            }
        }
    }

    fun submitSuratPeringatan(
        suratPeringatan: SuratPeringatan,
        imageFile: File?,
        pdfFile: File?
    ) {
        _isSubmitting.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.submitSuratPeringatan(
                suratPeringatan,
                imageFile,
                pdfFile,
                onSuccess = {
                    _isSubmitting.postValue(false)
                    _isSubmissionSuccessful.postValue(true)
                    Log.d("NasabahViewModel", "submitSuratPeringatan: Success")
                },
                onFailure = { error ->
                    _isSubmitting.postValue(false)
                    _isSubmissionSuccessful.postValue(false)
                    Log.e("NasabahViewModel", "submitSuratPeringatan: Error", error)
                }
            )
        }
    }
}





