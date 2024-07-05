package com.example.mobilemonitoringbankbpr.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.LocalStorage
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.data.AccountOfficer
import com.example.mobilemonitoringbankbpr.data.AdminKas
import com.example.mobilemonitoringbankbpr.data.Cabang
import com.example.mobilemonitoringbankbpr.data.Direksi
import com.example.mobilemonitoringbankbpr.data.KepalaCabang
import com.example.mobilemonitoringbankbpr.data.Supervisor
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.data.Wilayah
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    val supervisorList = MutableLiveData<List<Supervisor>>()
    val adminkasList = MutableLiveData<List<AdminKas>>()
    val accountofficerList = MutableLiveData<List<AccountOfficer>>()
    val jabatanLoaded = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val updateStatus = MutableLiveData<String>()

    init {
        jabatanLoaded.value = false
    }

    fun getUser(context: Context) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            // Menunggu hingga jabatanLoaded bernilai true
            while (jabatanLoaded.value == false) {
                delay(100)
            }
            if (jabatanLoaded.value == true) {
                val url = context.getString(R.string.api_server) + "/usermobile"
                val http = Http(context, url)
                http.setToken(true)
                http.send()

                val code = http.getStatusCode()
                if (code == 200) {
                    try {
                        val response = JSONObject(http.getResponse())
                        val id_user = response.getInt("id")
                        val name = response.getString("name")
                        val email = response.getString("email")
                        val jabatan = response.getInt("jabatan")
                        val jabatanName = jabatanMap[jabatan] ?: "Unknown"
                        val cabang = if (response.isNull("cabang")) null else response.getString("cabang")
                        val wilayah = if (response.isNull("wilayah")) null else response.getString("wilayah")
                        val id_direksi = if (response.isNull("id_direksi")) null else response.getString("id_direksi")
                        val id_kepala_cabang = if (response.isNull("id_kepala_cabang")) null else response.getString("id_kepala_cabang")
                        val id_supervisor = if (response.isNull("id_supervisor")) null else response.getString("id_supervisor")
                        val id_admin_kas = if (response.isNull("id_admin_kas")) null else response.getString("id_admin_kas")
                        val id_account_officer = if (response.isNull("id_account_officer")) null else response.getString("id_account_officer")

                        val userData = User(id_user, name, email, jabatanName, cabang, wilayah, id_direksi, id_kepala_cabang, id_supervisor, id_admin_kas, id_account_officer)
                        user.postValue(userData)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                isLoading.postValue(false)
            } else {
                isLoading.postValue(false)
                Log.e("AccountViewModel", "Failed to load jabatan data")
            }
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
    fun getSupervisor(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/supervisor"
            val http = Http(context, url)
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                try {
                    val response = JSONArray(http.getResponse())
                    val supervisorList = mutableListOf<Supervisor>()
                    for (i in 0 until response.length()) {
                        val supervisorObj = response.getJSONObject(i)
                        val id = supervisorObj.getInt("id_supervisor")
                        val name = supervisorObj.getString("nama_supervisor")
                        supervisorList.add(Supervisor(id, name))
                    }
                    this@AccountViewModel.supervisorList.postValue(supervisorList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
    fun getAdminKas(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/adminkas"
            val http = Http(context, url)
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                try {
                    val response = JSONArray(http.getResponse())
                    val adminkasList = mutableListOf<AdminKas>()
                    for (i in 0 until response.length()) {
                        val adminkasObj = response.getJSONObject(i)
                        val id = adminkasObj.getInt("id_admin_kas")
                        val name = adminkasObj.getString("nama_admin_kas")
                        adminkasList.add(AdminKas(id, name))
                    }
                    this@AccountViewModel.adminkasList.postValue(adminkasList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }
    fun getAccountOfficer(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/accountofficer"
            val http = Http(context, url)
            http.send()

            val code = http.getStatusCode()
            if (code == 200) {
                try {
                    val response = JSONArray(http.getResponse())
                    val accountofficerList = mutableListOf<AccountOfficer>()
                    for (i in 0 until response.length()) {
                        val accountofficerObj = response.getJSONObject(i)
                        val id = accountofficerObj.getInt("id_account_officer")
                        val name = accountofficerObj.getString("nama_account_officer")
                        accountofficerList.add(AccountOfficer(id, name))
                    }
                    this@AccountViewModel.accountofficerList.postValue(accountofficerList)
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
    fun updatePegawaiAdminKas(context: Context, idCabang: Int, idWilayah:Int, idSupervisor:Int) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/updatePegawaiAdminKas"
            val http = Http(context, url)
            val localStorage = LocalStorage(context)
            val userId = localStorage.userId // Ambil id_user dari local storage
            Log.d("LocalStorage", "UserId yang diambil: $userId")
            // Buat data JSON untuk dikirim
            val data = JSONObject().apply {
                put("id_cabang", idCabang)
                put("id_wilayah", idWilayah)
                put("id_supervisor", idSupervisor)
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
    fun updatePegawaiAccountOfficer(context: Context, idCabang: Int, idWilayah:Int, idAdminKas:Int) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val url = context.getString(R.string.api_server) + "/updatePegawaiAccountOfficer"
            val http = Http(context, url)
            val localStorage = LocalStorage(context)
            val userId = localStorage.userId // Ambil id_user dari local storage
            Log.d("LocalStorage", "UserId yang diambil: $userId")
            // Buat data JSON untuk dikirim
            val data = JSONObject().apply {
                put("id_cabang", idCabang)
                put("id_wilayah", idWilayah)
                put("id_admin_kas", idAdminKas)
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









