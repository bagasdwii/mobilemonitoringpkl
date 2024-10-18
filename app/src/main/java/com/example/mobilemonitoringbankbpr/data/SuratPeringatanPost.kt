package com.example.mobilemonitoringbankbpr.data

data class SuratPeringatanPost(
    val no: Long,
    val kategori: String? = null,
    val tingkat: Int,
    val dibuat: String? = null,
    val kembali: String? = null,
    val diserahkan: String,
    val bukti_gambar: String? = null,
    val scan_pdf: String? = null,
    val id_account_officer: Long

)
