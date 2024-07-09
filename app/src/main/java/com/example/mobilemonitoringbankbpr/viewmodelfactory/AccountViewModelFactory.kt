package com.example.mobilemonitoringbankbpr.viewmodelfactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobilemonitoringbankbpr.viewmodel.AccountViewModel

class AccountViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

