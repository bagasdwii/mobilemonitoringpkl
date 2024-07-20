package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import android.util.Log
import com.example.mobilemonitoringbankbpr.Http
import com.example.mobilemonitoringbankbpr.R
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import com.example.mobilemonitoringbankbpr.data.NasabahSp
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import retrofit2.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import retrofit2.Call
import org.json.JSONException
import retrofit2.Response
import java.io.File



class SuratRepository(private val context: Context) {

//    fun fetchNasabahList(): List<NasabahSp> {
//        Log.d("NasabahRepository", "fetchNasabahList: Start")
//        val url = context.getString(R.string.api_server) + "/nasabah"
//        val http = Http(context, url)
//
//        http.setMethod("GET")
//        http.setToken(true)
//
//        http.send()
//
//        val response = http.getResponse()
//        val nasabahList = mutableListOf<NasabahSp>()
//
//        response?.let {
//            try {
//                val jsonArray = JSONArray(it)
//                for (i in 0 until jsonArray.length()) {
//                    val jsonObject = jsonArray.getJSONObject(i)
//                    val nasabah = NasabahSp(
//                        no = jsonObject.getLong("no"),
//                        nama = jsonObject.getString("nama"),
//                        pokok = jsonObject.getString("pokok"),
//                        bunga = jsonObject.getString("bunga"),
//                        denda = jsonObject.getString("denda"),
//                        total = jsonObject.getInt("total"),
//                        keterangan = jsonObject.getString("keterangan"),
//                        ttd = jsonObject.getString("ttd"),
//                        kembali = jsonObject.getString("kembali"),
//                        cabang = jsonObject.getString("id_cabang"),
//                        wilayah = jsonObject.getString("id_wilayah"),
//                        adminkas = jsonObject.getString("id_admin_kas"),
//                        id_account_officer = jsonObject.getLong("id_account_officer")
//                    )
//                    nasabahList.add(nasabah)
//                }
//                Log.d("NasabahRepository", "fetchNasabahList: Success")
//            } catch (e: JSONException) {
//                Log.e("SuratRepository", "fetchNasabahList: Error parsing JSON", e)
//            }
//        } ?: run {
//            Log.e("SuratRepository", "fetchNasabahList: No response from server")
//        }
//
//        return nasabahList
//    }
    suspend fun fetchNasabahList(): List<NasabahSp> {
        Log.d("NasabahRepository", "fetchNasabahList: Start")

        val apiService = RetrofitClient.getServiceWithAuth(context)

        return try {
            val response = apiService.getNasabahList()
            Log.d("NasabahRepository", "fetchNasabahList: Success")
            response
        } catch (e: Exception) {
            Log.e("NasabahRepository", "fetchNasabahList: Error fetching data", e)
            emptyList()
        }
    }
    fun submitSuratPeringatan(
        suratPeringatan: SuratPeringatan,
        imageFile: File?,
        pdfFile: File?,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val apiService = RetrofitClient.getServiceWithAuth(context)

        val no = suratPeringatan.no.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val tingkat = suratPeringatan.tingkat.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val tanggal = suratPeringatan.tanggal.toRequestBody("text/plain".toMediaTypeOrNull())
        val keterangan = suratPeringatan.keterangan.toRequestBody("text/plain".toMediaTypeOrNull())
        val id_account_officer = suratPeringatan.id_account_officer.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val bukti_gambar = imageFile?.let {
            MultipartBody.Part.createFormData("bukti_gambar", it.name, it.asRequestBody("image/*".toMediaTypeOrNull()))
        }
        val scan_pdf = pdfFile?.let {
            MultipartBody.Part.createFormData("scan_pdf", it.name, it.asRequestBody("application/pdf".toMediaTypeOrNull()))
        }

        // Logging before making the request
        Log.d("SuratRepository", "Preparing to send SuratPeringatan data")
        Log.d("SuratRepository", "Data: no=${suratPeringatan.no}, tingkat=${suratPeringatan.tingkat}, tanggal=${suratPeringatan.tanggal}, keterangan=${suratPeringatan.keterangan}, id_account_officer=${suratPeringatan.id_account_officer}")

        bukti_gambar?.let {
            Log.d("SuratRepository", "buktiGambar: name=${it.body.contentType()}, length=${it.body.contentLength()} bytes")
        }

        scan_pdf?.let {
            Log.d("SuratRepository", "scanPdf: name=${it.body.contentType()}, length=${it.body.contentLength()} bytes")
        }

        val call = apiService.submitSuratPeringatan(
            no,
            tingkat,
            tanggal,
            keterangan,
            id_account_officer,
            bukti_gambar,
            scan_pdf
        )

        // Logging the endpoint and data
        val endpointUrl = call.request().url
        Log.d("SuratRepository", "submitSuratPeringatan: URL: $endpointUrl")
        Log.d("SuratRepository", "submitSuratPeringatan: Data: no=${suratPeringatan.no}, tingkat=${suratPeringatan.tingkat}, tanggal=${suratPeringatan.tanggal}, keterangan=${suratPeringatan.keterangan}, idAccountOfficer=${suratPeringatan.id_account_officer}")

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                    Log.d("SuratRepository", "submitSuratPeringatan: Success")
                } else {
                    onFailure(Exception("Failed with status code ${response.code()}"))
                    Log.e("SuratRepository", "submitSuratPeringatan: Failed with status code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure(t)
                Log.e("SuratRepository", "submitSuratPeringatan: Failed", t)
            }
        })
    }


}



