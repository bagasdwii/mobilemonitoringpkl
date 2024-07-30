package com.example.mobilemonitoringbankbpr.data

data class User(
    val id_user: Int,
    val name: String,
    val email: String,
    val jabatan: String,
    val cabang: String? = null,
    val wilayah:String?=null,
    val status:String?=null,
    val id_direksi: String? = null,
    val id_kepala_cabang: String? = null,
    val id_supervisor: String? = null,
    val id_admin_kas: String? = null,
    val id_account_officer: String? = null,
    val edit_user: List<User>

)
