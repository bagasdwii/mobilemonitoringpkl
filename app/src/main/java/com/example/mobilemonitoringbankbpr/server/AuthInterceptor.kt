package com.example.mobilemonitoringbankbpr.server

import android.content.Context
import com.example.mobilemonitoringbankbpr.LocalStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val localStorage = LocalStorage(context)
        val token = localStorage.token

        val newRequest = request.newBuilder()
            .addHeader("X-Authorization", "Bearer $token")
            .build()

        return chain.proceed(newRequest)
    }
}
