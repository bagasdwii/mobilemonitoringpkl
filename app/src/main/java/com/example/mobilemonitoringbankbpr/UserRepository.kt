package com.example.mobilemonitoringbankbpr

import android.content.Context
import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class UserRepository(private val context: Context) {

    fun login(email: String, password: String, callback: (Result<String>) -> Unit) {
        val params = JSONObject()
        try {
            params.put("email", email)
            params.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val data = params.toString()
        val url = context.getString(R.string.api_server) + "/loginmobile"

        Thread {
            val http = Http(context, url)
            http.setMethod("POST")
            http.setData(data)
            http.send()

            val code = http.getStatusCode()
            val response = http.getResponse()

            Log.d("USER_REPOSITORY", "HTTP Status Code: $code, Response: $response")

            if (code == 200) {
                try {
                    val jsonResponse = JSONObject(response)
                    val userId = jsonResponse.getInt("user_id")
                    val token = jsonResponse.getString("token")

                    // Simpan userId dan token ke LocalStorage
                    val localStorage = LocalStorage(context)
                    localStorage.userId = userId
                    localStorage.token = token
                    callback(Result.success(token))
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(Result.failure(e))
                }
            } else {
                try {
                    val jsonResponse = JSONObject(response)
                    val msg = jsonResponse.getString("message")
                    callback(Result.failure(Exception(msg)))
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(Result.failure(e))
                }
            }
        }.start()
    }
}

