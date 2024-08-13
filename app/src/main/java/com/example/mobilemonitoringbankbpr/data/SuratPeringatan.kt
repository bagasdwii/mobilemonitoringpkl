package com.example.mobilemonitoringbankbpr.data

data class SuratPeringatan(
    val no: Long,
    val tingkat: Int,
    val tanggal: String,
    val bukti_gambar: String? = null,
    val scan_pdf: String? = null,
    val id_account_officer: Long

)
