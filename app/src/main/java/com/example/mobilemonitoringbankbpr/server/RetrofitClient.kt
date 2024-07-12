package com.example.mobilemonitoringbankbpr.server

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.200.94:8000/"
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private fun provideOkHttpClientWithAuth(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor(context)) // Add AuthInterceptor here
            .build()
    }

    private fun provideOkHttpClientWithoutAuth(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    fun getServiceWithAuth(context: Context): ApiService {
        Log.d("RetrofitClient", "Base URL: $BASE_URL (With Auth)")

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(provideOkHttpClientWithAuth(context)) // Use OkHttpClient with Auth
            .build()

        return retrofit.create(ApiService::class.java)
    }

    fun getServiceWithoutAuth(): ApiService {
        Log.d("RetrofitClient", "Base URL: $BASE_URL (Without Auth)")

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(provideOkHttpClientWithoutAuth()) // Use OkHttpClient without Auth
            .build()

        return retrofit.create(ApiService::class.java)
    }
    private fun provideOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor(context)) // Add AuthInterceptor here
            .build()
    }

    fun getService(context: Context): ApiService {
        Log.d("RetrofitClient", "Base URL: $BASE_URL")

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient(context)) // Pass context to provideOkHttpClient
            .build()

        return retrofit.create(ApiService::class.java)
    }
}

