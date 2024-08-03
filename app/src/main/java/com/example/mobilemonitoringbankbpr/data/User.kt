package com.example.mobilemonitoringbankbpr.data

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val jabatan: String,
    val id_jabatan: Int?=null,
    val cabang: String? = null,
    val id_cabang: Int?=null,
    val wilayah:String?=null,
    val id_wilayah: Int?=null,
    val status:String?=null,
    val status_id: Int?=null,
    val id_direksi: String? = null,
    val direksi_id: Int?=null,
    val id_kepala_cabang: String? = null,
    val kepalacabang_id: Int?=null,
    val id_supervisor: String? = null,
    val supervisor_id: Int?=null,
    val id_admin_kas: String? = null,
    val adminkas_id: Int?=null,

)
