package com.example.mobilemonitoringbankbpr.data

data class ResponseLogin(
    val message: String,
    val token: String,
    val user_id: Int,
    val jabatan_id : Int
)
