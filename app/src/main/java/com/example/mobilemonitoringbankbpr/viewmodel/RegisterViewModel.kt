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

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val _jabatanList = MutableLiveData<List<Jabatan>>()
    val jabatanList: LiveData<List<Jabatan>> get() = _jabatanList

    fun fetchJabatanData(url: String) {
        Thread {
            val http = Http(getApplication(), url)
            http.setMethod("GET")
            http.send()

            if (http.getStatusCode() == 200) {
                try {
                    val response = JSONArray(http.getResponse())
                    val jabatanList = mutableListOf<Jabatan>()

                    for (i in 0 until response.length()) {
                        val jabatan = response.getJSONObject(i)
                        jabatanList.add(Jabatan(jabatan.getInt("id_jabatan"), jabatan.getString("nama_jabatan")))
                    }

                    _jabatanList.postValue(jabatanList)
                    Log.d("RegisterViewModel", "Jabatan data parsed successfully: $jabatanList")
                } catch (e: JSONException) {
                    Log.e("RegisterViewModel", "Failed to parse JSON response", e)
                    e.printStackTrace()
                }
            } else {
                Log.e("RegisterViewModel", "HTTP error: ${http.getStatusCode()}")
            }
        }.start()
    }

    fun registerUser(url: String, registerRequest: Register, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val params = JSONObject()
        try {
            params.put("name", registerRequest.name)
            params.put("email", registerRequest.email)
            params.put("password", registerRequest.password)
            params.put("jabatan_id", registerRequest.jabatanId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val data = params.toString()

        Thread {
            val http = Http(getApplication(), url)
            http.setMethod("POST")
            http.setData(data)
            http.send()

            val mainHandler = Handler(Looper.getMainLooper())
            mainHandler.post {
                if (http.getStatusCode() == 200 || http.getStatusCode() == 201) {
                    onSuccess()
                } else if (http.getStatusCode() == 422) {
                    try {
                        val response = JSONObject(http.getResponse())
                        val msg = response.getString("message")
                        onError(msg)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    onError("Error ${http.getStatusCode()}")
                }
            }
        }.start()
    }

}



