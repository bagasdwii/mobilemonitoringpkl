package com.example.mobilemonitoringbankbpr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.repository.SuratRepository
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.data.SuratPeringatanListNasabahDropdown
import com.example.mobilemonitoringbankbpr.data.SuratPeringatanPost

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class SuratViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SuratRepository(application)

    private val _nasabahList = MutableLiveData<List<SuratPeringatanListNasabahDropdown>>()
    val nasabahList: LiveData<List<SuratPeringatanListNasabahDropdown>> get() = _nasabahList

    private val _isSubmitting = MutableLiveData<Boolean>()
    val isSubmitting: LiveData<Boolean> get() = _isSubmitting

    private val _isSubmissionSuccessful = MutableLiveData<Boolean>()
    val isSubmissionSuccessful: LiveData<Boolean> get() = _isSubmissionSuccessful

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _submissionError = MutableLiveData<String?>()
    val submissionError: LiveData<String?> get() = _submissionError
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
        suratPeringatan: SuratPeringatanPost,
        imageFile: File?,
//        pdfFile: File?
    ) {
        _isSubmitting.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.submitSuratPeringatan(
                suratPeringatan,
                imageFile,
//                pdfFile,
                onSuccess = {
                    _isSubmitting.postValue(false)
                    _isSubmissionSuccessful.postValue(true)
                    _submissionError.postValue(null)
                    Log.d("SuratViewModel", "submitSuratPeringatan: Success")
                },
                onFailure = { error ->
                    _isSubmitting.postValue(false)
                    _isSubmissionSuccessful.postValue(false)
                    _submissionError.postValue(error.message)
                    Log.e("SuratViewModel", "submitSuratPeringatan: Error", error)
                }
            )
        }
    }
}






