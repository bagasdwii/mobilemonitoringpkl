package com.example.mobilemonitoringbankbpr.data

data class NasabahSp(
    val no: Long,
    val nama: String,
    val pokok: String,
    val bunga: String,
    val denda: String,
    val total: Int,
    val keterangan: String,
    val ttd: String,
    val kembali: String,
    val cabang: String,
    val kantorkas: String,
    val adminkas: String,
    val id_account_officer: Long
)
