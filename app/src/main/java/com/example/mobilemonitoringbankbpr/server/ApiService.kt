package com.example.mobilemonitoringbankbpr.server
import com.example.mobilemonitoringbankbpr.data.Jabatan
import com.example.mobilemonitoringbankbpr.data.Login
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.Register
import com.example.mobilemonitoringbankbpr.data.ResponseLogin
import com.example.mobilemonitoringbankbpr.data.ResponseRegister
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.data.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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
    @GET("api/jabatan")
    fun getJabatanData(): Call<List<Jabatan>>

    @POST("api/registermobile")
    fun registerUser(
        @Body registerRequest: Register
        ): Call<ResponseRegister>
    @POST("api/loginmobile")
    fun loginUser(
        @Body loginRequest: Login
    ): Call<ResponseLogin>

    @GET("api/usermobile")
    fun getUser(): Call<User>

    @POST("api/logoutmobile")
    fun logoutUser(): Call<Void>

    @GET("nasabahs")
    suspend fun getNasabahs(@Query("search") searchQuery: String): Response<List<Nasabah>>

    @GET("suratperingatan")
    suspend fun getSuratPeringatan(@Query("nasabah_no") nasabahNo: Long, @Query("tingkat") tingkat: Int): Response<SuratPeringatan>


}


