package com.example.mobilemonitoringbankbpr.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.Cabang
import com.example.mobilemonitoringbankbpr.data.Direksi
import com.example.mobilemonitoringbankbpr.data.KepalaCabang
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.data.Wilayah
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AccountViewModel : ViewModel() {
    val user = MutableLiveData<User>()
    val jabatanMap = mutableMapOf<Int, String>()
    val cabangList = MutableLiveData<List<Cabang>>()
    val wilayahList = MutableLiveData<List<Wilayah>>()
    val direksiList = MutableLiveData<List<Direksi>>()
    val kepalacabangList = MutableLiveData<List<KepalaCabang>>()
    val jabatanLoaded = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val updateStatus = MutableLiveData<String>()

    init {
        jabatanLoaded.value = false
    }
    fun getUser(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            val url = context.getString(R.string.api_server) + "/user"
            val http = Http(context, url)
            http.setToken(true)

            http.send()
            Log.d("AccountViewModel", "Fetching user data started")

            val code = http.getStatusCode()
            Log.d("AccountViewModel", "HTTP status code for getUser: $code")

            if (code == 200) {

                try {
                    val response = JSONObject(http.getResponse())
                    Log.d("AccountViewModel", "User data response: $response")

                    val id = response.getInt("id")
                    val name = response.getString("name")
                    val email = response.getString("email")
                    val jabatan = response.getInt("jabatan")
                    val jabatanName = jabatanMap[jabatan] ?: "Unknown"
                    val user = User(id, name, email, jabatanName)
                    this@AccountViewModel.user.postValue(user)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            isLoading.postValue(false)
        }
    }
    fun getUserKepalaCabang(context: Context) {
        isLoading.value = true
        Log.d("AccountViewModel", "Fetching user data started")
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/usermobile"
            val http = Http(context, url)
            http.setToken(true)
            http.send()

            val code = http.getStatusCode()
            Log.d("AccountViewModel", "HTTP status code for getUser: $code")
            if (code == 200) {
                try {
                    val response = JSONObject(http.getResponse())
                    Log.d("AccountViewModel", "User data response: $response")
                    val id_user = response.getInt("id")
                    val name = response.getString("name")
                    val email = response.getString("email")
                    val jabatan = response.getInt("jabatan")
                    val jabatanName = jabatanMap[jabatan] ?: "Unknown"
                    val cabang = if (response.isNull("cabang")) null else response.getString("cabang")
                    val wilayah = if (response.isNull("wilayah")) null else response.getString("wilayah")
                    val id_direksi = if (response.isNull("id_direksi")) null else response.getString("id_direksi")
                    val id_kepala_cabang = if (response.isNull("id_kepala_cabang")) null else response.getString("id_kepala_cabang")

                    val userData = User(id_user, name, email, jabatanName, cabang, wilayah,id_direksi,id_kepala_cabang,)

                    user.postValue(userData)
                    Log.d("AccountViewModel", "User data updated")

                } catch (e: JSONException) {
                    Log.e("AccountViewModel", "JSON parsing error: ${e.message}")
                    e.printStackTrace()
                }
            }
            isLoading.postValue(false)
            Log.d("AccountViewModel", "Fetching user data ended")
        }
    }

    fun getUserSupervisor(context: Context) {
        isLoading.value = true
        Log.d("AccountViewModel", "Fetching user super data started")
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/usermobile"
            val http = Http(context, url)
            http.setToken(true)
            http.send()

            val code = http.getStatusCode()
            Log.d("AccountViewModel", "HTTP status code for getUser: $code")
            if (code == 200) {
                try {
                    val response = JSONObject(http.getResponse())
                    Log.d("AccountViewModel", "User data response super: $response")
                    val id_user = response.getInt("id")
                    val name = response.getString("name")
                    val email = response.getString("email")
                    val jabatan = response.getInt("jabatan")
                    val jabatanName = jabatanMap[jabatan] ?: "Unknown"
                    val cabang = if (response.isNull("cabang")) null else response.getString("cabang")
                    val wilayah = if (response.isNull("wilayah")) null else response.getString("wilayah")
                    val id_kepala_cabang = if (response.isNull("id_kepala_cabang")) null else response.getString("id_kepala_cabang")

                    val userData = User(id_user, name, email, jabatanName, cabang, wilayah, id_kepala_cabang)

                    user.postValue(userData)
                    Log.d("AccountViewModel", "User data updated")

                } catch (e: JSONException) {
                    Log.e("AccountViewModel", "JSON parsing error: ${e.message}")
                    e.printStackTrace()
                }
            }
            isLoading.postValue(false)
            Log.d("AccountViewModel", "Fetching user data ended")
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
                    val response = JSONArray(http.getResponse())
                    for (i in 0 until response.length()) {
                        val jabatanObj = response.getJSONObject(i)
                        val id = jabatanObj.getInt("id_jabatan")
                        val name = jabatanObj.getString("nama_jabatan")
                        jabatanMap[id] = name
                    }
                    jabatanLoaded.postValue(true)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getCabang(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/cabang"
            val http = Http(context, url)
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                try {
                    val response = JSONArray(http.getResponse())
                    val cabangList = mutableListOf<Cabang>()
                    for (i in 0 until response.length()) {
                        val cabangObj = response.getJSONObject(i)
                        val id = cabangObj.getInt("id_cabang")
                        val name = cabangObj.getString("nama_cabang")
                        cabangList.add(Cabang(id, name))
                    }
                    this@AccountViewModel.cabangList.postValue(cabangList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getWilayah(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/wilayah"
            val http = Http(context, url)
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                try {
                    val response = JSONArray(http.getResponse())
                    val wilayahList = mutableListOf<Wilayah>()
                    for (i in 0 until response.length()) {
                        val wilayahObj = response.getJSONObject(i)
                        val id = wilayahObj.getInt("id_wilayah")
                        val name = wilayahObj.getString("nama_wilayah")
                        wilayahList.add(Wilayah(id, name))
                    }
                    this@AccountViewModel.wilayahList.postValue(wilayahList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
    fun getDireksi(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/direksi"
            val http = Http(context, url)
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                try {
                    val response = JSONArray(http.getResponse())
                    val direksiList = mutableListOf<Direksi>()
                    for (i in 0 until response.length()) {
                        val direksiObj = response.getJSONObject(i)
                        val id = direksiObj.getInt("id_direksi")
                        val name = direksiObj.getString("nama")
                        direksiList.add(Direksi(id, name))
                    }
                    this@AccountViewModel.direksiList.postValue(direksiList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
    fun getKepalaCabang(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/kepalacabang"
            val http = Http(context, url)
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                try {
                    val response = JSONArray(http.getResponse())
                    val kepalacabangList = mutableListOf<KepalaCabang>()
                    for (i in 0 until response.length()) {
                        val kepalacabangObj = response.getJSONObject(i)
                        val id = kepalacabangObj.getInt("id_kepala_cabang")
                        val name = kepalacabangObj.getString("nama_kepala_cabang")
                        kepalacabangList.add(KepalaCabang(id, name))
                    }
                    this@AccountViewModel.kepalacabangList.postValue(kepalacabangList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
    fun updatePegawaiKepalaCabang(context: Context, idCabang: Int, idDireksi:Int) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/updatePegawaiKepalaCabang"
            val http = Http(context, url)
            val localStorage = LocalStorage(context)
            val userId = localStorage.userId // Ambil id_user dari local storage
            Log.d("LocalStorage", "UserId yang diambil: $userId")
            // Buat data JSON untuk dikirim
            val data = JSONObject().apply {
                put("id_cabang", idCabang)
                put("id_direksi", idDireksi)
                put("id_user", userId)
            }

            // Tambahkan log untuk melihat data yang dikirim
            Log.d("UPDATE_DATA", "Data yang dikirim: $data")

            http.setMethod("POST")
            http.setData(data.toString())
            http.setToken(true) // Jika menggunakan token
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                // Update berhasil
                Log.d("Update", "Update berhasil $code")
                updateStatus.postValue("success")
            } else {
                // Gagal update
                Log.e("Update", "Update gagal, status code: $code, response: ${http.getResponse()}")
                updateStatus.postValue("fail")
            }

            isLoading.postValue(false)
        }
    }

    fun updatePegawaiSupervisor(context: Context, idCabang: Int, idWilayah:Int, idKepalaCabang:Int) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/updatePegawaiSupervisor"
            val http = Http(context, url)
            val localStorage = LocalStorage(context)
            val userId = localStorage.userId // Ambil id_user dari local storage
            Log.d("LocalStorage", "UserId yang diambil: $userId")
            // Buat data JSON untuk dikirim
            val data = JSONObject().apply {
                put("id_cabang", idCabang)
                put("id_wilayah", idWilayah)
                put("id_kepala_cabang", idKepalaCabang)
                put("id_user", userId)
            }

            // Tambahkan log untuk melihat data yang dikirim
            Log.d("UPDATE_DATA", "Data yang dikirim: $data")

            http.setMethod("POST")
            http.setData(data.toString())
            http.setToken(true) // Jika menggunakan token
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                // Update berhasil
                Log.d("Update", "Update berhasil $code")
                updateStatus.postValue("success")
            } else {
                // Gagal update
                Log.e("Update", "Update gagal, status code: $code, response: ${http.getResponse()}")
                updateStatus.postValue("fail")
            }

            isLoading.postValue(false)
        }
    }

}









