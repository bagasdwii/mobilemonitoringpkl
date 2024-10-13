package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import android.util.Log
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import com.example.mobilemonitoringbankbpr.data.ResponseSuratPeringatan
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.data.SuratPeringatanListNasabahDropdown
import retrofit2.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import org.json.JSONObject
import retrofit2.Response
import java.io.File

class SuratRepository(private val context: Context) {

    fun submitSuratPeringatan(
        suratPeringatan: SuratPeringatan,
        imageFile: File?,
//        pdfFile: File?,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val apiService = RetrofitClient.getServiceWithAuth(context)

        val no = suratPeringatan.no.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val kategori = suratPeringatan.kategori.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val tingkat = suratPeringatan.tingkat.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val diserahkan = suratPeringatan.diserahkan.toRequestBody("text/plain".toMediaTypeOrNull())
        val id_account_officer = suratPeringatan.id_account_officer.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val bukti_gambar = imageFile?.let {
            MultipartBody.Part.createFormData("bukti_gambar", it.name, it.asRequestBody("image/*".toMediaTypeOrNull()))
        }
//        val scan_pdf = pdfFile?.let {
//            MultipartBody.Part.createFormData("scan_pdf", it.name, it.asRequestBody("application/pdf".toMediaTypeOrNull()))
//        }

        // Logging before making the request
        Log.d("SuratRepository", "Preparing to send SuratPeringatan data")
        Log.d("SuratRepository", "Data: no=${suratPeringatan.no}, tingkat=${suratPeringatan.tingkat}, tanggal=${suratPeringatan.diserahkan}, id_account_officer=${suratPeringatan.id_account_officer}")

        bukti_gambar?.let {
            Log.d("SuratRepository", "buktiGambar: name=${it.body.contentType()}, length=${it.body.contentLength()} bytes")
        }

//        scan_pdf?.let {
//            Log.d("SuratRepository", "scanPdf: name=${it.body.contentType()}, length=${it.body.contentLength()} bytes")
//        }

        val call = apiService.updateSuratPeringatan(
            no,
            tingkat,
            kategori,
            diserahkan,
//            id_account_officer,
            bukti_gambar,
//            scan_pdf
        )

        // Logging the endpoint and data
        val endpointUrl = call.request().url
        Log.d("SuratRepository", "submitSuratPeringatan: URL: $endpointUrl")
        Log.d("SuratRepository", "submitSuratPeringatan: Data: no=${suratPeringatan.no}, tingkat=${suratPeringatan.tingkat}, tanggal=${suratPeringatan.diserahkan}, idAccountOfficer=${suratPeringatan.id_account_officer}")

        call.enqueue(object : Callback<ResponseSuratPeringatan> {
            override fun onResponse(call: Call<ResponseSuratPeringatan>, response: Response<ResponseSuratPeringatan>) {
                if (response.isSuccessful) {
                    onSuccess()
                    Log.d("SuratRepository", "submitSuratPeringatan: Success")
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorResponse).getString("error")
                    } catch (e: Exception) {
                        "Unknown error"
                    }
                    onFailure(Exception(errorMessage))
                    Log.e("SuratRepository", "submitSuratPeringatan: Error ${response.code()}: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ResponseSuratPeringatan>, t: Throwable) {
                onFailure(t)
                Log.e("SuratRepository", "submitSuratPeringatan: Failed", t)
            }
        })


    }

    suspend fun fetchNasabahList(): List<SuratPeringatanListNasabahDropdown> {
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



}


