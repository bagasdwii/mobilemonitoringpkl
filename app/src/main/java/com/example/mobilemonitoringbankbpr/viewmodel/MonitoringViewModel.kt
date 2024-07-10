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
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder

class MonitoringViewModel : ViewModel() {
    private val _nasabahs = MutableLiveData<List<Nasabah>>()
    val nasabahs: LiveData<List<Nasabah>> get() = _nasabahs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _suratPeringatan = MutableLiveData<SuratPeringatan>()
    val suratPeringatan: LiveData<SuratPeringatan> get() = _suratPeringatan

    private var currentPage = 1

    fun getNasahabs(searchQuery: String, context: Context) {
        _isLoading.value = true
        Log.d("MonitoringViewModel", "Fetching nasabahs started with search query: $searchQuery on page: $currentPage")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = context.getString(R.string.api_server) + "/nasabahs?search=" + URLEncoder.encode(searchQuery, "UTF-8") + "&page=$currentPage"
                val http = Http(context, url)
                http.setMethod("GET")
                http.setToken(true)
                http.send()

                val code = http.getStatusCode()
                Log.d("MonitoringViewModel", "HTTP status code: $code")
                if (code == 200) {
                    val response = JSONObject(http.getResponse()!!)
                    val nasabahsList = mutableListOf<Nasabah>()

                    val nasabahsArray = response.getJSONArray("data")
                    for (i in 0 until nasabahsArray.length()) {
                        val nasabahJson = nasabahsArray.getJSONObject(i)
                        val suratPeringatanJson = nasabahJson.optJSONObject("surat_peringatan")

                        val nasabah = Nasabah(
                            no = nasabahJson.getLong("no"),
                            nama = nasabahJson.getString("nama"),
                            cabang = nasabahJson.getString("nama_cabang"),
                            suratPeringatan = suratPeringatanJson?.let {
                                SuratPeringatan(
                                    no = it.getLong("no"),
                                    tingkat = it.getInt("tingkat"),
                                    tanggal = it.getString("tanggal"),
                                    keterangan = it.getString("keterangan"),
                                    bukti_gambar = it.getString("bukti_gambar"),
                                    scan_pdf = it.getString("scan_pdf"),
                                    id_account_officer = it.getLong("id_account_officer")
                                )
                            }
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

    fun setPage(page: Int) {
        currentPage = page
    }

    fun getCurrentPage(): Int {
        return currentPage
    }
}



