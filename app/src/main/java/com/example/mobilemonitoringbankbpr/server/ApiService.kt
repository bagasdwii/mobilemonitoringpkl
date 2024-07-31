package com.example.mobilemonitoringbankbpr.server
import com.example.mobilemonitoringbankbpr.data.ConnectionResponse
import com.example.mobilemonitoringbankbpr.data.Jabatan
import com.example.mobilemonitoringbankbpr.data.Login
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.NasabahSp
import com.example.mobilemonitoringbankbpr.data.Register
import com.example.mobilemonitoringbankbpr.data.ResponseLogin
import com.example.mobilemonitoringbankbpr.data.ResponseMonitoring
import com.example.mobilemonitoringbankbpr.data.ResponseRegister
import com.example.mobilemonitoringbankbpr.data.ResponseSuratPeringatan
import com.example.mobilemonitoringbankbpr.data.ResponseUserList
import com.example.mobilemonitoringbankbpr.data.SuratPeringatan
import com.example.mobilemonitoringbankbpr.data.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("api/checkconnection")
    fun checkConnection(): Call<ConnectionResponse>

    @Multipart
    @POST("api/surat_peringatan")
    fun submitSuratPeringatan(
        @Part("no") no: RequestBody,
        @Part("tingkat") tingkat: RequestBody,
        @Part("tanggal") tanggal: RequestBody,
        @Part("id_account_officer") idAccountOfficer: RequestBody,
        @Part bukti_gambar: MultipartBody.Part?,
        @Part scan_pdf: MultipartBody.Part?
    ): Call<ResponseSuratPeringatan>

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


    @GET("api/nasabahs")
    suspend fun getNasabahs(
        @Query("search") search: String,
        @Query("page") page: Int
    ): Response<ResponseMonitoring>

    @GET("api/usermobileadmin")
    suspend fun getUser(
        @Query("search") search: String,
        @Query("page") page: Int
    ): Response<ResponseUserList>

    @GET("api/nasabah")
    suspend fun getNasabahList(): List<NasabahSp>

    @GET("surat-peringatan/gambar/{filename}")
    suspend fun getGambar(@Path("filename") filename: String): Response<ResponseBody>

    @GET("surat-peringatan/pdf/{filename}")
    suspend fun getPdf(@Path("filename") filename: String): Response<ResponseBody>
}