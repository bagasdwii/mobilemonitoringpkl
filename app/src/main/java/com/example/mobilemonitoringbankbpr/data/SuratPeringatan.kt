package com.example.mobilemonitoringbankbpr.data

data class SuratPeringatan(
    val no: Long,
    val kategori: String? = null,
    val tingkat: Int,
    val dibuat: String,
    val kembali: String,
    val diserahkan: String,
    val bukti_gambar: String? = null,
    val scan_pdf: String? = null,
    val id_account_officer: Long

)
