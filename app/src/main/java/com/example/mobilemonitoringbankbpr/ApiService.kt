package com.example.mobilemonitoringbankbpr
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("api/surat_peringatan")
    fun submitSuratPeringatan(
        @Part("no") no: RequestBody,
        @Part("tingkat") tingkat: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("keterangan") keterangan: RequestBody,
        @Part("idAccountOfficer") idAccountOfficer: RequestBody,
        @Part buktiGambar: MultipartBody.Part?,
        @Part scanPdf: MultipartBody.Part?
    ): Call<Void>
}


