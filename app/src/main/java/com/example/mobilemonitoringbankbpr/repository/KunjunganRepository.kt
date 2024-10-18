package com.example.mobilemonitoringbankbpr.repository

import android.content.Context
import android.util.Log
import com.example.mobilemonitoringbankbpr.data.Kunjungan
import com.example.mobilemonitoringbankbpr.data.ResponseKunjungan
import com.example.mobilemonitoringbankbpr.data.ResponseSuratPeringatan
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.data.SuratPeringatanListNasabahDropdown
import com.example.mobilemonitoringbankbpr.server.ApiService
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class KunjunganRepository(private val apiService: ApiService,  private val context: Context) {

    suspend fun getKunjunganList(noNasabah: Long): List<Kunjungan> {
        return apiService.getKunjunganList(noNasabah)
    }
    fun submitKunjungan(
        kunjungan: Kunjungan,
        imageFile: File?,
        onSuccess: () -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val apiService = RetrofitClient.getServiceWithAuth(context)

        val no_nasabah = kunjungan.no.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val keterangan = kunjungan.keterangan.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val koordinat = kunjungan.koordinat.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val tanggal = kunjungan.tanggal.toRequestBody("text/plain".toMediaTypeOrNull())

        val bukti_gambar = imageFile?.let {
            MultipartBody.Part.createFormData("bukti_gambar", it.name, it.asRequestBody("image/*".toMediaTypeOrNull()))
        }

        // Logging before making the request
        Log.d("KunjunganRepository", "Preparing to send submitKunjungan data")
        Log.d("KunjunganRepository", "Data: no=${kunjungan.no}, tingkat=${kunjungan.keterangan}, tanggal=${kunjungan.tanggal}")

        bukti_gambar?.let {
            Log.d("KunjunganRepository", "buktiGambar: name=${it.body.contentType()}, length=${it.body.contentLength()} bytes")
        }

        val call = apiService.tambahKunjungan(
            no_nasabah,
            koordinat,
            keterangan,
            tanggal,
            bukti_gambar,
        )
        // Logging the endpoint and data
        val endpointUrl = call.request().url
        Log.d("KunjunganRepository", "submitKunjungan: URL: $endpointUrl")
        Log.d("KunjunganRepository", "submitKunjungan: Data: no=${kunjungan.no}, tingkat=${kunjungan.keterangan}, tanggal=${kunjungan.tanggal}")

        call.enqueue(object : Callback<ResponseKunjungan> {
            override fun onResponse(call: Call<ResponseKunjungan>, response: Response<ResponseKunjungan>) {
                if (response.isSuccessful) {
                    onSuccess()
                    Log.d("KunjunganRepository", "submitKunjungan: Success")
                } else {
                    val errorResponse = response.errorBody()?.string()
                    val errorMessage = try {
                        JSONObject(errorResponse).getString("error")
                    } catch (e: Exception) {
                        "Unknown error"
                    }
                    onFailure(Exception(errorMessage))
                    Log.e("KunjunganRepository", "submitKunjungan: Error ${response.code()}: $errorMessage")
                }
            }
            override fun onFailure(call: Call<ResponseKunjungan>, t: Throwable) {
                onFailure(t)
                Log.e("KunjunganRepository", "submitKunjungan: Failed", t)
            }
        })
    }
    suspend fun fetchNasabahList(): List<Kunjungan> {
        Log.d("KunjunganRepository", "fetchNasabahList: Start")

        val apiService = RetrofitClient.getServiceWithAuth(context)

        return try {
            val response = apiService.getKunjunganListNasabah()
            Log.d("KunjunganRepository", "fetchNasabahList: Success")
            response
        } catch (e: Exception) {
            Log.e("KunjunganRepository", "fetchNasabahList: Error fetching data", e)
            emptyList()
        }
    }
}
