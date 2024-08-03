package com.example.mobilemonitoringbankbpr.repository


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobilemonitoringbankbpr.data.AdminKas
import com.example.mobilemonitoringbankbpr.data.AllDataResponse
import com.example.mobilemonitoringbankbpr.data.Cabang
import com.example.mobilemonitoringbankbpr.data.Direksi
import com.example.mobilemonitoringbankbpr.data.Jabatan
import com.example.mobilemonitoringbankbpr.data.KepalaCabang
import com.example.mobilemonitoringbankbpr.data.Nasabah
import com.example.mobilemonitoringbankbpr.data.NasabahSp
import com.example.mobilemonitoringbankbpr.data.Supervisor
import com.example.mobilemonitoringbankbpr.data.UpdateUser
import com.example.mobilemonitoringbankbpr.data.UpdateUserResponse
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.data.Wilayah
import com.example.mobilemonitoringbankbpr.server.ApiService
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminRepository(private val apiService: ApiService, private val context: Context){

    suspend fun getUser(searchQuery: String, page: Int): Result<List<User>> {
        return try {
            Log.d("AdminRepository", "Fetching User with query: $searchQuery, page: $page")
            val response = apiService.getUser(searchQuery, page)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("AdminRepository", "User fetched successfully")
                    Result.success(it.data)
                } ?: run {
                    Log.e("AdminRepository", "Error parsing response")
                    Result.failure(Exception("Error parsing response"))
                }
            } else {
                Log.e("AdminRepository", "Error fetching User, status code: ${response.code()}")
                Result.failure(Exception("Error fetching User, status code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("AdminRepository", "Exception occurred: ${e.message}", e)
            Result.failure(e)
        }
    }
//    suspend fun updateUser(userId: Int, updateUser: UpdateUser): Result<UpdateUserResponse> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val apiService = RetrofitClient.getServiceWithAuth(context)
//                val response: Response<UpdateUserResponse> = apiService.updateUser(userId, updateUser)
//
//                // Log raw response data
//                val responseBodyString = response.raw().peekBody(Long.MAX_VALUE).string()
//                Log.d("AdminRepository", "Response Code: ${response.code()}")
//                Log.d("AdminRepository", "Response Body: $responseBodyString")
//
//                if (response.isSuccessful) {
//                    Log.d("AdminRepository", "Successful response: $responseBodyString")
//                    response.body()?.let {
//                        Result.success(it)
//                    } ?: run {
//                        Log.e("AdminRepository", "Response body is null")
//                        Result.failure(Exception("Response body is null"))
//                    }
//                } else {
//                    val errorBodyString = response.errorBody()?.string()
//                    Log.d("AdminRepository", "Error Response: $errorBodyString")
//
//                    val errorMessage = if (!errorBodyString.isNullOrEmpty()) {
//                        try {
//                            val jsonObject = JSONObject(errorBodyString)
//                            val message = jsonObject.optString("message", "Unknown error")
//                            val errors = jsonObject.optJSONObject("errors")
//                            if (errors != null) {
//                                val errorMessages = StringBuilder(message)
//                                errors.keys().forEach { key ->
//                                    val messagesArray = errors.optJSONArray(key)
//                                    if (messagesArray != null) {
//                                        for (i in 0 until messagesArray.length()) {
//                                            errorMessages.append("\n").append(messagesArray.optString(i))
//                                        }
//                                    }
//                                }
//                                errorMessages.toString()
//                            } else {
//                                message
//                            }
//                        } catch (e: JSONException) {
//                            Log.e("AdminRepository", "Failed to parse error JSON", e)
//                            "Failed to parse server error response"
//                        }
//                    } else {
//                        "Unknown error"
//                    }
//                    Log.d("AdminRepository", "Error Message: $errorMessage")
//                    Result.failure(Exception(errorMessage))
//                }
//            } catch (e: Exception) {
//                Log.e("AdminRepository", "updateUser exception", e)
//                Result.failure(e)
//            }
//        }
//    }

    fun updateUser(userId: Int, updateUser: UpdateUser, callback: (Result<UpdateUserResponse>) -> Unit) {
        val apiService = RetrofitClient.getServiceWithAuth(context)

        apiService.updateUser(userId, updateUser).enqueue(object : Callback<UpdateUserResponse> {
            override fun onResponse(call: Call<UpdateUserResponse>, response: Response<UpdateUserResponse>) {
                Log.d("UserRepository", "Response Code: ${response.code()}")
                Log.d("UserRepository", "Response Body: ${response.body()}")

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("UserRepository", "Successful response: ${response.body()}")
                        callback(Result.success(it))
                    } ?: run {
                        Log.e("UserRepository", "Response body is null")
                        callback(Result.failure(Exception("Response body is null")))
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    Log.d("UserRepository", "Error Response: $errorBodyString")

                    val errorMessage = if (!errorBodyString.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBodyString)
                            val message = jsonObject.optString("message", "Unknown error")
                            val errors = jsonObject.optJSONObject("errors")
                            if (errors != null) {
                                val errorMessages = StringBuilder(message)
                                errors.keys().forEach { key ->
                                    val messagesArray = errors.optJSONArray(key)
                                    if (messagesArray != null) {
                                        for (i in 0 until messagesArray.length()) {
                                            errorMessages.append("\n").append(messagesArray.optString(i))
                                        }
                                    }
                                }
                                errorMessages.toString()
                            } else {
                                message
                            }
                        } catch (e: JSONException) {
                            Log.e("UserRepository", "Failed to parse error JSON", e)
                            "Failed to parse server error response"
                        }
                    } else {
                        "Unknown error"
                    }
                    Log.d("UserRepository", "Error Message: $errorMessage")
                    callback(Result.failure(Exception(errorMessage)))
                }
            }

            override fun onFailure(call: Call<UpdateUserResponse>, t: Throwable) {
                Log.e("UserRepository", "Failed to update user", t)
                callback(Result.failure(t))
            }
        })
    }






    suspend fun fetchAllData(): AllDataResponse? {
        return try {
            val response = apiService.getAllData()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

}