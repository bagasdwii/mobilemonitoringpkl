package com.example.mobilemonitoringbankbpr

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class Http(private val context: Context, private var url: String) {
    private var method: String = "GET"
    private var data: JSONObject = JSONObject()
    private var response: String? = null
    private var statusCode: Int? = null
    private var token: Boolean = false
    private val localStorage = LocalStorage(context)

    fun setMethod(method: String) {
        this.method = method.toUpperCase()
    }

    fun setData(data: String) {
        this.data = JSONObject(data)
    }

    fun setToken(token: Boolean) {
        this.token = token
    }

    fun addParameter(key: String, value: String) {
        data.put(key, value)
    }

    fun getResponse(): String? {
        return response
    }

    fun getStatusCode(): Int? {
        return statusCode
    }

//    fun send() {
//        try {
//            val url = URL(this.url)
//            val connection = url.openConnection() as HttpURLConnection
//            connection.requestMethod = method
//            connection.setRequestProperty("Content-Type", "application/json")
//            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest")
//
//            if (token) {
//                connection.setRequestProperty("Authorization", "Bearer ${localStorage.token}")
//            }
//
//            if (method != "GET") {
//                connection.doOutput = true
//                val os: OutputStream = connection.outputStream
//                os.write(data.toString().toByteArray())
//                os.flush()
//                os.close()
//            }
//
//            statusCode = connection.responseCode
//            val isr = if (statusCode in 200..299) {
//                InputStreamReader(connection.inputStream)
//            } else {
//                InputStreamReader(connection.errorStream)
//            }
//            val br = BufferedReader(isr)
//            val sb = StringBuilder()
//            var line: String?
//
//            while (br.readLine().also { line = it } != null) {
//                sb.append(line)
//            }
//            br.close()
//            response = sb.toString()
//
//            // Log the response
//            Log.d("HTTP_RESPONSE", "Response Code: $statusCode, Response: $response")
//        } catch (e: IOException) {
//            e.printStackTrace()
//            Log.e("HTTP_ERROR", "IOException: ${e.message}")
//        }
//    }
    fun send() {
    try {
        val url = URL(this.url)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("X-Requested-With", "XMLHttpRequest")

        if (token) {
            val authToken = localStorage.token
            Log.d("HTTP", "Auth Token: $authToken")
            connection.setRequestProperty("Authorization", "Bearer $authToken")
        }

        if (method != "GET") {
            connection.doOutput = true
            val os: OutputStream = connection.outputStream
            os.write(data.toString().toByteArray())
            os.flush()
            os.close()
        }

        statusCode = connection.responseCode
        val isr = if (statusCode in 200..299) {
            InputStreamReader(connection.inputStream)
        } else {
            InputStreamReader(connection.errorStream)
        }
        val br = BufferedReader(isr)
        val sb = StringBuilder()
        var line: String?

        while (br.readLine().also { line = it } != null) {
            sb.append(line)
        }
        br.close()
        response = sb.toString()

        // Log the response
        Log.d("HTTP_RESPONSE", "Response Code: $statusCode, Response: $response")
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("HTTP_ERROR", "IOException: ${e.message}")
    }
}
}
