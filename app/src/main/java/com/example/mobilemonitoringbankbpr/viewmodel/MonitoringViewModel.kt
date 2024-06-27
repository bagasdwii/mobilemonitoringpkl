package com.example.mobilemonitoringbankbpr.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Nasabah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class MonitoringViewModel : ViewModel(){
    private val _nasabahs = MutableLiveData<List<Nasabah>>()
    val nasabahs: LiveData<List<Nasabah>> get() = _nasabahs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun getNasahabs(searchQuery: String, context: Context) {
        _isLoading.value = true
        Log.d("MonitoringViewModel", "Fetching nasabahs started with search query: $searchQuery")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = context.getString(R.string.api_server) + "/nasabahs?search=" + URLEncoder.encode(searchQuery, "UTF-8")
                val http = Http(context, url)
                http.setMethod("GET")
                http.setToken(true)
                http.send()

                val code = http.getStatusCode()
                Log.d("MonitoringViewModel", "HTTP status code: $code")
                if (code == 200) {
                    val response = JSONArray(http.getResponse()!!)
                    val nasabahsList = mutableListOf<Nasabah>()

                    for (i in 0 until response.length()) {
                        val nasabahJson = response.getJSONObject(i)
                        val nasabah = Nasabah(
                            no = nasabahJson.getLong("no"),
                            nama = nasabahJson.getString("nama"),
                            cabang = nasabahJson.getString("nama_cabang")
                        )
                        nasabahsList.add(nasabah)
                    }
                    _nasabahs.postValue(nasabahsList)
                    Log.d("MonitoringViewModel", "Nasabahs fetched successfully: ${nasabahsList.size} items")
                } else {
                    // Handle error
                    Log.e("MonitoringViewModel", "Error fetching nasabahs, status code: $code")
                }
            } catch (e: Exception) {
                // Handle exception
                Log.e("MonitoringViewModel", "Exception while fetching nasabahs", e)
            } finally {
                _isLoading.postValue(false)
                Log.d("MonitoringViewModel", "Fetching nasabahs completed")
            }
        }
    }

}