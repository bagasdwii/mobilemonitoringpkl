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
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class MonitoringViewModel : ViewModel() {
    private val _nasabahs = MutableLiveData<List<Nasabah>>()
    val nasabahs: LiveData<List<Nasabah>> get() = _nasabahs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _suratPeringatan = MutableLiveData<SuratPeringatan>()
    val suratPeringatan: LiveData<SuratPeringatan> get() = _suratPeringatan

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
                    Log.e("MonitoringViewModel", "Error fetching nasabahs, status code: $code")
                }
            } catch (e: Exception) {
                Log.e("MonitoringViewModel", "Exception while fetching nasabahs", e)
            } finally {
                _isLoading.postValue(false)
                Log.d("MonitoringViewModel", "Fetching nasabahs completed")
            }
        }
    }

    fun checkSuratPeringatan(nasabahNo: Long, tingkat: Int, context: Context, callback: (Boolean) -> Unit) {
        Log.d("MonitoringViewModel", "Checking Surat Peringatan for Nasabah No: $nasabahNo, Tingkat: $tingkat")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = context.getString(R.string.api_server) + "/suratperingatan?nasabah_no=$nasabahNo"
                val http = Http(context, url)
                http.setMethod("GET")
                http.setToken(true)
                http.send()

                val code = http.getStatusCode()
                val response = http.getResponse()

                if (code == 200 && response!!.isNotEmpty()) {
                    val jsonResponse = JSONObject(response)
                    val fetchedTingkat = jsonResponse.getInt("tingkat")

                    // Compare the fetched tingkat with the parameter tingkat
                    if (tingkat != fetchedTingkat) {
                        Log.d("MonitoringViewModel", "Tingkat parameter ($tingkat) does not match fetched tingkat ($fetchedTingkat). Skipping.")
                        withContext(Dispatchers.Main) {
                            callback(false)
                        }
                        return@launch
                    }

                    val hasSuratPeringatan = true // Assuming you have logic here to determine if surat peringatan exists

                    // Log the HTTP response
                    Log.d("MonitoringViewModel", "HTTP status code: $code, response: $response, hasSuratPeringatan: $hasSuratPeringatan")

                    withContext(Dispatchers.Main) {
                        callback(hasSuratPeringatan)
                    }
                } else {
                    Log.e("MonitoringViewModel", "Error fetching Surat Peringatan, status code: $code")
                    withContext(Dispatchers.Main) {
                        callback(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("MonitoringViewModel", "Exception while checking Surat Peringatan", e)
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }



    fun getSuratPeringatan(nasabahNo: Long, tingkat: Int, context: Context) {
        Log.d("MonitoringViewModel", "Fetching Surat Peringatan for Nasabah No: $nasabahNo, Tingkat: $tingkat")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = context.getString(R.string.api_server) + "/suratperingatan?nasabah_no=$nasabahNo&tingkat=$tingkat"
                val http = Http(context, url)
                http.setMethod("GET")
                http.setToken(true)
                http.send()

                val code = http.getStatusCode()
                Log.d("MonitoringViewModel", "HTTP status code: $code")
                if (code == 200) {
                    val response = JSONObject(http.getResponse()!!)
                    val suratPeringatan = SuratPeringatan(
                        no = response.getLong("no"),
                        tingkat = response.getInt("tingkat"),
                        tanggal = response.getString("tanggal"),
                        keterangan = response.getString("keterangan"),
                        buktiGambar = context.getString(R.string.api_server) + response.optString("bukti_gambar"),
                        scanPdf = context.getString(R.string.api_server) + response.optString("scan_pdf"),
                        idAccountOfficer = response.getLong("id_account_officer")
                    )
                    _suratPeringatan.postValue(suratPeringatan)
                    Log.d("MonitoringViewModel", "Surat Peringatan fetched successfully: $suratPeringatan")
                } else {
                    Log.e("MonitoringViewModel", "Error fetching Surat Peringatan, status code: $code")
                }
            } catch (e: Exception) {
                Log.e("MonitoringViewModel", "Exception while fetching Surat Peringatan", e)
            }
        }
    }

}
