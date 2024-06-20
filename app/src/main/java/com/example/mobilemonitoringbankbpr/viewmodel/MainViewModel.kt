package com.example.mobilemonitoringbankbpr.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.User
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainViewModel : ViewModel() {
    val user = MutableLiveData<User>()
    val jabatanMap = mutableMapOf<Int, String>()
    val jabatanLoaded = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()


    init {
        jabatanLoaded.value = false
    }

    fun getUser(context: Context) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/usermobile"
            val http = Http(context, url)
            http.setToken(true)
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                try {
                    val response = JSONObject(http.getResponse())
                    val name = response.getString("name")
                    val email = response.getString("email")
                    val jabatanId = response.getInt("jabatan_id")
                    val jabatanName = jabatanMap[jabatanId] ?: "Unknown"
                    user.postValue(User(name, email, jabatanName))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            isLoading.postValue(false)
        }
    }

    fun getJabatan(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/jabatan"
            val http = Http(context, url)
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                try {
                    val response = JSONArray(http.getResponse()) // Menggunakan JSONArray
                    for (i in 0 until response.length()) {
                        val jabatanObj = response.getJSONObject(i)
                        val id = jabatanObj.getInt("id_jabatan") // id_jabatan
                        val name = jabatanObj.getString("nama_jabatan") // nama_jabatan
                        jabatanMap[id] = name
                    }
                    jabatanLoaded.postValue(true) // Menandai bahwa data jabatan telah diambil
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
}



