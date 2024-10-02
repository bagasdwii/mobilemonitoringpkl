package com.example.mobilemonitoringbankbpr.repository


import android.content.Context
import android.util.Log
import com.example.mobilemonitoringbankbpr.data.AllDataResponse
import com.example.mobilemonitoringbankbpr.data.UpdateUser
import com.example.mobilemonitoringbankbpr.data.UpdateUserResponse
import com.example.mobilemonitoringbankbpr.data.User
import com.example.mobilemonitoringbankbpr.server.ApiService
import com.example.mobilemonitoringbankbpr.server.RetrofitClient
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