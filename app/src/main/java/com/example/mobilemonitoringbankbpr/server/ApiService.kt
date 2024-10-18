package com.example.mobilemonitoringbankbpr.server
import com.example.mobilemonitoringbankbpr.data.AdminKas
import com.example.mobilemonitoringbankbpr.data.AllDataResponse
import com.example.mobilemonitoringbankbpr.data.Cabang
import com.example.mobilemonitoringbankbpr.data.ConnectionResponse
import com.example.mobilemonitoringbankbpr.data.Direksi
import com.example.mobilemonitoringbankbpr.data.Jabatan
import com.example.mobilemonitoringbankbpr.data.KepalaCabang
import com.example.mobilemonitoringbankbpr.data.Login
import com.example.mobilemonitoringbankbpr.data.Register
import com.example.mobilemonitoringbankbpr.data.ResponseLogin
import com.example.mobilemonitoringbankbpr.data.ResponseMonitoring
import com.example.mobilemonitoringbankbpr.data.ResponseRegister
import com.example.mobilemonitoringbankbpr.data.ResponseSuratPeringatan
import com.example.mobilemonitoringbankbpr.data.ResponseUserList
import com.example.mobilemonitoringbankbpr.data.Supervisor
import com.example.mobilemonitoringbankbpr.data.SuratPeringatanListNasabahDropdown
import com.example.mobilemonitoringbankbpr.data.UpdateUser
import com.example.mobilemonitoringbankbpr.data.UpdateUserResponse
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.data.KantorKas
import com.example.mobilemonitoringbankbpr.data.Kunjungan
import com.example.mobilemonitoringbankbpr.data.ResponseKunjungan
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
//    @Multipart
//    @POST("api/surat_peringatan")
//    fun submitSuratPeringatan(
//        @Part("no") no: RequestBody,
//        @Part("tingkat") tingkat: RequestBody,
//        @Part("tanggal") tanggal: RequestBody,
//        @Part("id_account_officer") idAccountOfficer: RequestBody,
//        @Part bukti_gambar: MultipartBody.Part?,
//        @Part scan_pdf: MultipartBody.Part?
//    ): Call<ResponseSuratPeringatan>
    @Multipart
    @POST("api/surat_peringatan/update")
    fun updateSuratPeringatan(
        @Part("no") no: RequestBody,
        @Part("tingkat") tingkat: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("diserahkan") tanggal: RequestBody,
        @Part bukti_gambar: MultipartBody.Part?
    ): Call<ResponseSuratPeringatan>
    @Multipart
    @POST("api/kunjungan/tambah")
    fun tambahKunjungan(
        @Part("no_nasabah") no: RequestBody,
        @Part("koordinat") koordinat: RequestBody,

        @Part("keterangan") keterangan: RequestBody,
        @Part("tanggal") tanggal: RequestBody,

        @Part bukti_gambar: MultipartBody.Part?
    ): Call<ResponseKunjungan>

    @GET("api/usermobile")
    fun getUser(): Call<User>
    @GET("api/checkconnection")
    fun checkConnection(): Call<ConnectionResponse>
    @GET("api/kunjungan/get/{no_nasabah}")
    suspend fun getKunjunganList(@Path("no_nasabah") noNasabah: Long): List<Kunjungan>


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



    @POST("api/logoutmobile")
    fun logoutUser(): Call<Void>


    @GET("api/nasabahs")
    suspend fun getNasabahs(
        @Query("search") search: String,
        @Query("cabang") cabang: String,
        @Query("page") page: Int
    ): Response<ResponseMonitoring>


    @GET("api/nasabah")
    suspend fun getNasabahList(): List<SuratPeringatanListNasabahDropdown>
    @GET("api/kunjungan/list")
    suspend fun getKunjunganListNasabah(): List<Kunjungan>
    @GET("api/usermobileadmin")
    suspend fun getUser(
        @Query("search") search: String,
        @Query("page") page: Int
    ): Response<ResponseUserList>

    @GET("api/cabang")
    suspend fun getCabangList(): List<Cabang>
    @GET("api/kantorkas")
    suspend fun getKantorKasList(): List<KantorKas>
    @GET("api/jabatanauth")
    suspend fun getJabatanList(): List<Jabatan>
    @GET("api/direksi")
    suspend fun getDireksiList(): List<Direksi>
    @GET("api/supervisor")
    suspend fun getSupervisorList(): List<Supervisor>
    @GET("api/adminkas")
    suspend fun getAdminkasList(): List<AdminKas>
    @GET("api/kepalacabang")
    suspend fun getKepalacabangList(): List<KepalaCabang>
    @GET("api/status")
    suspend fun getStatusList(): List<Cabang>
    @GET("surat-peringatan/gambar/{filename}")
    suspend fun getGambar(@Path("filename") filename: String): Response<ResponseBody>

    @GET("surat-peringatan/pdf/{filename}")
    suspend fun getPdf(@Path("filename") filename: String): Response<ResponseBody>
    @GET("api/alldata")
    suspend fun getAllData(): Response<AllDataResponse>
    @PUT("api/user/update/{id}")
    fun updateUser(
        @Path("id") id: Int,
        @Body updateUser: UpdateUser
    ): Call<UpdateUserResponse>


}